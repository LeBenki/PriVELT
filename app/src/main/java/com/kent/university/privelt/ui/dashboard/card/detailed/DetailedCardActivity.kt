/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card.detailed

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.events.LaunchListDataEvent
import com.kent.university.privelt.model.Card
import com.kent.university.privelt.ui.data.DataActivity
import com.kent.university.privelt.ui.gdpr.GDPRActivity
import com.kent.university.privelt.ui.login.LoginActivity
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import kotlinx.android.synthetic.main.activity_detailed_card.*
import net.neferett.webviewsextractor.model.UserDataTypes
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class DetailedCardActivity : BaseActivity() {
    private var card: Card? = null

    override val activityLayout: Int
        get() = R.layout.activity_detailed_card

    override fun configureViewModel() {
    }

    override fun configureDesign(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            card = savedInstanceState.getSerializable(PARAM_CARD) as Card?
        } else if (intent != null) {
            card = intent.getSerializableExtra(PARAM_CARD) as Card
        }
        titleTV!!.text = card!!.title
        var progress = 0
        for ((_, number) in card!!.metrics) progress += number
        risk_progress!!.progress = progress * 100 / 200
        if (!card!!.isService) {
            val userDataType = UserDataTypes.getUserDataType(card!!.title.toUpperCase(Locale.ROOT))
            logo!!.setImageResource(userDataType.res)
        } else {
            val priVELTApplication = logo!!.context.applicationContext as PriVELTApplication
            logo!!.setImageResource(priVELTApplication.serviceHelper!!.getResIdWithName(card!!.title))
        }
        var riskValue = progress
        if (riskValue > 100) riskValue = 100
        when {
            riskValue < 20 -> privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Low")
            riskValue < 60 -> privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Medium")
            else -> privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "High")
        }
        title = card!!.title

        gdpr!!.setOnClickListener { startActivity(Intent(this, GDPRActivity::class.java)) }
        configureRecyclerView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(PARAM_CARD, card)
    }

    private fun configureRecyclerView() {
        if (card!!.metrics.size != 0) {
            progress_circular!!.visibility = View.GONE
        }
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recycler_view_metrics!!.layoutManager = layoutManager
        val detailedCardAdapter = DetailedCardAdapter(card!!.metrics, !card!!.isService)
        recycler_view_metrics!!.adapter = detailedCardAdapter
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onLaunchListData(event: LaunchListDataEvent) {
        val intent = Intent(this, DataActivity::class.java)
        if (card!!.isService) {
            intent.putExtra(LoginActivity.PARAM_SERVICE, card!!.title)
            intent.putExtra(DataActivity.PARAM_TYPE, event.card)
        } else {
            intent.putExtra(LoginActivity.PARAM_SERVICE, event.card)
            intent.putExtra(DataActivity.PARAM_TYPE, card!!.title)
        }
        startActivity(intent)
    }

    companion object {
        const val PARAM_CARD = "PARAM_CARD"
    }
}