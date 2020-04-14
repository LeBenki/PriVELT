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
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.api.DataExtraction.processDataExtraction
import com.kent.university.privelt.api.ServiceHelper
import com.kent.university.privelt.base.BaseFragment
import com.kent.university.privelt.events.ChangeWatchListStatusEvent
import com.kent.university.privelt.events.DetailedCardEvent
import com.kent.university.privelt.events.UpdateCredentialsEvent
import com.kent.university.privelt.model.Card
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.ui.dashboard.DashboardActivity
import com.kent.university.privelt.ui.dashboard.card.FilterAlertDialog.FilterDialogListener
import com.kent.university.privelt.ui.dashboard.card.detailed.DetailedCardActivity
import com.kent.university.privelt.ui.login.LoginActivity
import com.kent.university.privelt.ui.risk_value.RiskValueActivity
import com.kent.university.privelt.utils.CardManager
import com.kent.university.privelt.utils.WatchListHelper
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import kotlinx.android.synthetic.main.fragment_service.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*


class CardFragment : BaseFragment(), FilterDialogListener {
    private var cardViewModel: CardViewModel? = null
    private var subscribedServices: ArrayList<Service>? = null
    private var userData: ArrayList<UserData>? = null
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
        get() {
            cardViewModel!!.services!!.observe(this, Observer { services: List<Service> -> updateServices(services) })
        }

    private fun updateServices(services: List<Service>) {
        subscribedServices = ArrayList(services)
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        if (subscribedServices!!.size == 0) {
            baseView.noServices!!.visibility = View.VISIBLE
            cardAdapter!!.updateCards(ArrayList(0))
        }
        else {
            baseView.noServices!!.visibility = View.GONE
            cardAdapter!!.updateCards(CardManager.cardsFilter(userData, subscribedServices!!, filters, watchListHelper!!.watchList))
        }
        cardAdapter!!.notifyDataSetChanged()
    }

    private fun updateOverallRiskValue() {
        var riskValue = userData!!.size
        if (riskValue > 100) riskValue = 100
        when {
            riskValue < 20 -> baseView.privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Low")
            riskValue < 60 -> baseView.privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Medium")
            else -> baseView.privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "High")
        }
        baseView.progressBar!!.progress = riskValue
    }

    private fun setupAddButton() {
        baseView.addService?.setOnClickListener {
            val services: List<String> = serviceHelper?.getRemainingServices(subscribedServices!!)!!
            if (services.isEmpty()) {
                Toast.makeText(this@CardFragment.context, R.string.already_added_all, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val adp = ArrayAdapter(context!!,
                    android.R.layout.simple_spinner_item, serviceHelper!!.getRemainingServices(subscribedServices!!))
            val sp = Spinner(context)
            sp.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            sp.setPadding(50, 50, 50, 50)
            sp.adapter = adp
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.choose_service)
            builder.setView(sp)
            builder.setPositiveButton(R.string.choose) { _: DialogInterface?, _: Int ->
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

    private fun setUpRecyclerView(view: View) {
        subscribedServices = ArrayList()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        baseView.servicesList?.layoutManager = layoutManager
        cardAdapter = CardAdapter()
        baseView.servicesList?.adapter = cardAdapter
    }

    override val fragmentLayout: Int
        get() = R.layout.fragment_service

    override fun configureViewModel() {
        cardViewModel = getViewModel(CardViewModel::class.java)
        cardViewModel?.init()
    }

    override fun configureDesign(view: View) {
        setUpRecyclerView(view)
        setupAddButton()
        services
        getUserData()
        baseView.progressBar?.setOnClickListener { startActivity(Intent(activity, RiskValueActivity::class.java)) }
        enableSwipeToDeleteAndUndo()
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
                    service.user = user!!
                    service.password = password!!
                }
                object : AsyncTask<Void?, Void?, Service?>() {
                    override fun doInBackground(vararg voids: Void?): Service? {
                        if (requestCode == REQUEST_LOGIN) {
                            cardViewModel!!.insertService(service)
                            return cardViewModel!!.getServiceWithName(serviceName = service.name)
                        } else {
                            cardViewModel!!.updateService(service)
                        }
                        return null
                    }

                    override fun onPostExecute(serviceP: Service?) {
                        super.onPostExecute(serviceP)
                        if (!service.isPasswordSaved)
                            processDataExtraction(ServiceHelper(context), serviceP!!, user, password, context!!.applicationContext)
                        else (activity as DashboardActivity?)!!.launchService()
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

    override fun onDialogPositiveClick(selectedItems: BooleanArray?) {
        filters = selectedItems!!
        updateRecyclerView()
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                val item: Card = cardAdapter?.getData()?.get(position)!!
                val tmpService: Service? = subscribedServices?.last { it.name == item.title }

                AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.unsubscribe)
                        .setMessage(R.string.unsubscribe_confirmation)
                        .setPositiveButton(R.string.yes) { _, _ -> run { cardViewModel?.deleteService(tmpService) } }
                        .setNegativeButton(R.string.no) { _: DialogInterface, _: Int ->
                            run {
                                cardAdapter?.removeItem(position)
                                cardAdapter?.restoreItem(item, position)
                            }
                        }
                        .show()
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(baseView.servicesList)
    }

    companion object {
        private const val REQUEST_LOGIN = 765
        private const val REQUEST_EDIT_LOGIN = 7654
    }
}