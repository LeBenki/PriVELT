/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.R
import com.kent.university.privelt.events.ChangeWatchListStatusEvent
import com.kent.university.privelt.events.DetailedCardEvent
import com.kent.university.privelt.events.UpdateCredentialsEvent
import com.kent.university.privelt.model.Card
import com.kent.university.privelt.ui.dashboard.card.data_metrics.DataMetricsAdapter
import com.kent.university.privelt.ui.risk_value.BarActivity
import com.kent.university.privelt.ui.risk_value.RiskValueActivity
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import kotlinx.android.synthetic.main.cell_service.view.*
import net.neferett.webviewsextractor.model.UserDataTypes
import org.greenrobot.eventbus.EventBus
import java.util.*

internal class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val dataMetricsAdapter: DataMetricsAdapter
    fun bind(card: Card) {
        itemView.title!!.text = SentenceAdapter.capitaliseFirstLetter(card.title)
        if (card.isService) {
            val priVELTApplication = itemView.title!!.context.applicationContext as PriVELTApplication
            itemView.image_service!!.setImageResource(priVELTApplication.serviceHelper!!.getResIdWithName(card.title))
            itemView.settings!!.visibility = View.VISIBLE
            itemView.settings!!.setOnClickListener { EventBus.getDefault().post(UpdateCredentialsEvent(card.title)) }
            itemView.cardService!!.strokeColor = ContextCompat.getColor(itemView.context, R.color.colorAccent)
        } else {
            val userDataType = UserDataTypes.getUserDataType(card.title.toUpperCase(Locale.ROOT))
            itemView.image_service!!.setImageResource(userDataType.res)
            itemView.settings!!.visibility = View.GONE
            itemView.cardService!!.strokeColor = Color.WHITE
        }

        itemView.watch_icon!!.setOnClickListener {
            EventBus.getDefault().post(ChangeWatchListStatusEvent(card.title))
            card.isWatched = !card.isWatched
            itemView.watch_icon!!.setColorFilter(if (card.isWatched) ContextCompat.getColor(itemView.context, R.color.colorAccent) else ContextCompat.getColor(itemView.context, android.R.color.black))
        }

        itemView.setOnClickListener { EventBus.getDefault().post(DetailedCardEvent(card)) }
        itemView.watch_icon!!.setColorFilter(if (card.isWatched) ContextCompat.getColor(itemView.context, R.color.colorAccent) else ContextCompat.getColor(itemView.context, android.R.color.black))

        var total = 0
        for (item in card.metrics) total += item.number
        if (card.metrics.size != 0) {
            //TODO: 200 HARDCODED (MAX DATA)
            itemView.risk_progress!!.progress = total * 100 / 200
            itemView.risk_progress!!.setOnClickListener {
                val intent = Intent(itemView.risk_progress!!.context, BarActivity::class.java)
                if (card.isService) intent.putExtra(RiskValueActivity.PARAM_SERVICE, card.title) else intent.putExtra(RiskValueActivity.PARAM_DATA, card.title)
                itemView.risk_progress!!.context.startActivity(intent)
            }
            itemView.metric_rv!!.visibility = View.VISIBLE
            itemView.service_value!!.visibility = View.VISIBLE
            itemView.service_value!!.text = total.toString()
            dataMetricsAdapter.setDataMetrics(card)
            dataMetricsAdapter.notifyDataSetChanged()
        } else {
            itemView.risk_progress!!.progress = 0
            itemView.risk_progress!!.setOnClickListener(null)
            itemView.metric_rv!!.visibility = View.GONE
            itemView.service_value!!.visibility = View.GONE
            itemView.risk_progress!!.setOnClickListener(null)
        }
        var riskValue = total
        if (riskValue > 100) riskValue = 100
        when {
            riskValue < 20 -> itemView.privacyValue!!.text = SentenceAdapter.adapt(itemView.context.resources.getString(R.string.global_privacy_value), "Low")
            riskValue < 60 -> itemView.privacyValue!!.text = SentenceAdapter.adapt(itemView.context.resources.getString(R.string.global_privacy_value), "Medium")
            else -> itemView.privacyValue!!.text = SentenceAdapter.adapt(itemView.context.resources.getString(R.string.global_privacy_value), "High")
        }
    }

    init {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        itemView.metric_rv!!.layoutManager = layoutManager
        dataMetricsAdapter = DataMetricsAdapter()
        itemView.metric_rv!!.adapter = dataMetricsAdapter
    }
}