/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kent.university.privelt.R
import com.kent.university.privelt.api.DataExtraction.processDataExtraction
import com.kent.university.privelt.api.ServiceHelper
import com.kent.university.privelt.base.BaseFragment
import com.kent.university.privelt.events.ChangeWatchListStatusEvent
import com.kent.university.privelt.events.DetailedCardEvent
import com.kent.university.privelt.events.UpdateCredentialsEvent
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.ui.dashboard.DashboardActivity
import com.kent.university.privelt.ui.dashboard.card.CardFragment
import com.kent.university.privelt.ui.dashboard.card.FilterAlertDialog.FilterDialogListener
import com.kent.university.privelt.ui.dashboard.card.detailed.DetailedCardActivity
import com.kent.university.privelt.ui.login.LoginActivity
import com.kent.university.privelt.ui.risk_value.RiskValueActivity
import com.kent.university.privelt.utils.CardManager
import com.kent.university.privelt.utils.WatchListHelper
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class CardFragment : BaseFragment(), FilterDialogListener {
    private var cardViewModel: CardViewModel? = null
    private var subscribedServices: ArrayList<Service>? = null
    private var userData: ArrayList<UserData>? = null

    @JvmField
    @BindView(R.id.recycler_view_services)
    var servicesList: RecyclerView? = null

    @JvmField
    @BindView(R.id.no_services)
    var noService: TextView? = null

    @JvmField
    @BindView(R.id.add_service)
    var addService: FloatingActionButton? = null

    @JvmField
    @BindView(R.id.global_privacy_value)
    var privacyValue: TextView? = null

    @JvmField
    @BindView(R.id.risk_progress_overall)
    var progressBar: ProgressBar? = null
    private var watchListHelper: WatchListHelper? = null
    private var cardAdapter: CardAdapter? = null
    private lateinit var filters: BooleanArray
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        filters = FilterAlertDialog.getFilters(activity!!.getSharedPreferences(FilterAlertDialog.KEY_SHARED, Context.MODE_PRIVATE))
        watchListHelper = WatchListHelper(activity!!.getSharedPreferences(FilterAlertDialog.KEY_SHARED, Context.MODE_PRIVATE))
    }

    private fun getUserData() {
        cardViewModel!!.userData?.observe(this, Observer { userData: List<UserData> -> updateUserData(userData) })
    }

    private fun updateUserData(userData: List<UserData>) {
        this.userData = ArrayList(userData)
        updateOverallRiskValue()
        updateRecyclerView()
    }

    private val services: Unit
        private get() {
            cardViewModel!!.services!!.observe(this, Observer { services: List<Service> -> updateServices(services) })
        }

    private fun updateServices(services: List<Service>) {
        subscribedServices = ArrayList(services)
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        if (subscribedServices!!.size == 0) noService!!.visibility = View.VISIBLE else {
            noService!!.visibility = View.GONE
            cardAdapter!!.updateCards(CardManager.cardsFilter(userData, subscribedServices, filters, watchListHelper!!.watchList))
            cardAdapter!!.notifyDataSetChanged()
        }
    }

    private fun updateOverallRiskValue() {
        var riskValue = userData!!.size
        if (riskValue > 100) riskValue = 100
        if (riskValue < 20) privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Low") else if (riskValue < 60) privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Medium") else privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "High")
        progressBar!!.progress = riskValue
    }

    private fun setupAddButton() {
        addService!!.setOnClickListener { view: View? ->
            val services: List<String> = serviceHelper.getRemainingServices(subscribedServices)
            if (services.isEmpty()) {
                Toast.makeText(this@CardFragment.context, R.string.already_added_all, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val adp = ArrayAdapter(context!!,
                    android.R.layout.simple_spinner_item, serviceHelper.getRemainingServices(subscribedServices))
            val sp = Spinner(context)
            sp.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            sp.setPadding(50, 50, 50, 50)
            sp.adapter = adp
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.choose_service)
            builder.setView(sp)
            builder.setPositiveButton(R.string.choose) { dialogInterface: DialogInterface?, i: Int ->
                editCredentials(
                        Service(sp.selectedItem.toString(), false, "", "", ""), REQUEST_LOGIN)
            }
            builder.create().show()
        }
    }

    private fun editCredentials(service: Service, requestCode: Int) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(LoginActivity.PARAM_SERVICE, service)
        startActivityForResult(intent, requestCode)
    }

    private fun setUpRecyclerView() {
        subscribedServices = ArrayList()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        servicesList!!.layoutManager = layoutManager
        cardAdapter = CardAdapter()
        servicesList!!.adapter = cardAdapter
    }

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_service
    }

    override fun configureViewModel() {
        cardViewModel = getViewModel(CardViewModel::class.java)
        cardViewModel?.init()
    }

    override fun configureDesign() {
        setUpRecyclerView()
        setupAddButton()
        services
        getUserData()
        progressBar!!.setOnClickListener { v: View? -> startActivity(Intent(activity, RiskValueActivity::class.java)) }
    }

    @SuppressLint("StaticFieldLeak")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_LOGIN || requestCode == REQUEST_EDIT_LOGIN) && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val service = data.getSerializableExtra(LoginActivity.PARAM_SERVICE) as Service
                val user = data.getStringExtra(LoginActivity.PARAM_USER)
                val password = data.getStringExtra(LoginActivity.PARAM_PASSWORD)
                if (service.isPasswordSaved) {
                    service.user = user
                    service.password = password
                }
                object : AsyncTask<Void?, Void?, Void?>() {
                    override fun doInBackground(vararg voids: Void?): Void? {
                        if (requestCode == REQUEST_LOGIN) {
                            cardViewModel!!.insertService(service)
                        } else {
                            cardViewModel!!.updateService(service)
                        }
                        return null
                    }

                    override fun onPostExecute(aVoid: Void?) {
                        super.onPostExecute(aVoid)
                        if (!service.isPasswordSaved) processDataExtraction(ServiceHelper(context), service, user, password, context!!.applicationContext) else (activity as DashboardActivity?)!!.launchService()
                    }
                }.execute()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onEditCredentials(event: UpdateCredentialsEvent) {
        for (service in subscribedServices!!) if (service.name == event.service) editCredentials(service, REQUEST_EDIT_LOGIN)
    }

    @Subscribe
    fun onDetailedCard(event: DetailedCardEvent) {
        val intent = Intent(context, DetailedCardActivity::class.java)
        intent.putExtra(DetailedCardActivity.PARAM_CARD, event.card)
        startActivity(intent)
    }

    @Subscribe
    fun onCardAddedToWatchList(event: ChangeWatchListStatusEvent) {
        watchListHelper!!.changeWatchListStatus(event.cardName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.filter) {
            val cdf = FilterAlertDialog(this)
            cdf.show(fragmentManager!!, "")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDialogPositiveClick(selectedItems: BooleanArray) {
        filters = selectedItems
        updateRecyclerView()
    }

    companion object {
        private const val REQUEST_LOGIN = 765
        private const val REQUEST_EDIT_LOGIN = 7654
    }
}