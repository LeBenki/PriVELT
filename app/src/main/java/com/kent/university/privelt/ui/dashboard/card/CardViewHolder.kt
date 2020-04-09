/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.card.MaterialCardView
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
import net.neferett.webviewsextractor.model.UserDataTypes
import org.greenrobot.eventbus.EventBus

internal class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    @BindView(R.id.image_service)
    var imageService: ImageView? = null

    @JvmField
    @BindView(R.id.title_service)
    var title: TextView? = null

    @JvmField
    @BindView(R.id.settings)
    var settings: ImageView? = null

    @JvmField
    @BindView(R.id.watch_icon)
    var watchIcon: ImageView? = null

    @JvmField
    @BindView(R.id.metric_rv)
    var metrics: RecyclerView? = null

    @JvmField
    @BindView(R.id.service_value)
    var totalMetrics: TextView? = null

    @JvmField
    @BindView(R.id.risk_progress)
    var riskProgress: ProgressBar? = null

    @JvmField
    @BindView(R.id.cardService)
    var cardView: MaterialCardView? = null
    private val dataMetricsAdapter: DataMetricsAdapter
    fun bind(card: Card) {
        title!!.text = SentenceAdapter.capitaliseFirstLetter(card.title)
        if (card.isService) {
            val priVELTApplication = title!!.context.applicationContext as PriVELTApplication
            imageService!!.setImageResource(priVELTApplication.serviceHelper!!.getResIdWithName(card.title))
        } else {
            val userDataType = UserDataTypes.valueOf(card.title.toUpperCase())
            imageService!!.setImageResource(userDataType.res)
        }
        if (card.isService) {
            settings!!.visibility = View.VISIBLE
            settings!!.setOnClickListener { view: View? -> EventBus.getDefault().post(UpdateCredentialsEvent(card.title)) }
            cardView!!.strokeColor = itemView.context.resources.getColor(R.color.colorAccent)
        } else {
            settings!!.visibility = View.GONE
        }
        watchIcon!!.setOnClickListener { view: View? ->
            EventBus.getDefault().post(ChangeWatchListStatusEvent(card.title))
            card.isWatched = !card.isWatched
            watchIcon!!.setColorFilter(if (card.isWatched) itemView.context.resources.getColor(R.color.colorAccent) else itemView.context.resources.getColor(android.R.color.black))
        }
        itemView.setOnClickListener { view: View? -> EventBus.getDefault().post(DetailedCardEvent(card)) }
        watchIcon!!.setColorFilter(if (card.isWatched) itemView.context.resources.getColor(R.color.colorAccent) else itemView.context.resources.getColor(android.R.color.black))
        var total = 0
        for (item in card.metrics) total += item.number
        if (card.metrics.size != 0) {
            //TODO: 200 HARDCODED (MAX DATA)
            riskProgress!!.progress = total * 100 / 200
            riskProgress!!.setOnClickListener { v: View? ->
                val intent = Intent(riskProgress!!.context, BarActivity::class.java)
                if (card.isService) intent.putExtra(RiskValueActivity.PARAM_SERVICE, card.title) else intent.putExtra(RiskValueActivity.PARAM_DATA, card.title)
                riskProgress!!.context.startActivity(intent)
            }
            metrics!!.visibility = View.VISIBLE
            totalMetrics!!.visibility = View.VISIBLE
            totalMetrics!!.text = total.toString()
            dataMetricsAdapter.setDataMetrics(card.metrics, !card.isService)
            dataMetricsAdapter.notifyDataSetChanged()
        } else {
            riskProgress!!.progress = 0
            riskProgress!!.setOnClickListener(null)
            metrics!!.visibility = View.GONE
            totalMetrics!!.visibility = View.GONE
            riskProgress!!.setOnClickListener(null)
        }
    }

    init {
        ButterKnife.bind(this, itemView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        metrics!!.layoutManager = layoutManager
        dataMetricsAdapter = DataMetricsAdapter()
        metrics!!.adapter = dataMetricsAdapter
    }
}