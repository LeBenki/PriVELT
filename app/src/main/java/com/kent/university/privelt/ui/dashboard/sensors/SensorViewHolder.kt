/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.events.LaunchDetailedSensorEvent
import com.kent.university.privelt.model.Sensor
import com.kent.university.privelt.ui.dashboard.sensors.chart.sensor.SensorChartActivity
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import kotlinx.android.synthetic.main.cell_application.view.*
import kotlinx.android.synthetic.main.cell_application.view.privacyValue
import kotlinx.android.synthetic.main.cell_application.view.risk_progress
import kotlinx.android.synthetic.main.cell_application.view.title
import org.greenrobot.eventbus.EventBus

internal class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(sensor: Sensor) {
        itemView.image_sensor!!.setImageResource(sensor.resId)
        itemView.sensor_value!!.text = sensor.getApplications().size.toString()
        itemView.setOnClickListener { EventBus.getDefault().post(LaunchDetailedSensorEvent(sensor)) }
        itemView.title.text = sensor.title

        itemView.risk_progress!!.progress = sensor.getApplications().size

        itemView.risk_progress!!.setOnClickListener {
            val intent = Intent(itemView.risk_progress!!.context, SensorChartActivity::class.java)
            intent.putExtra(SensorChartActivity.PARAM_PERMISSION, sensor.title)
            itemView.risk_progress!!.context.startActivity(intent)
        }

        var riskValue = sensor.getApplications().size
        if (riskValue > 100) riskValue = 100
        when {
            riskValue < 20 -> itemView.privacyValue!!.text = SentenceAdapter.adapt(itemView.context.resources.getString(R.string.global_privacy_value), "Low")
            riskValue < 60 -> itemView.privacyValue!!.text = SentenceAdapter.adapt(itemView.context.resources.getString(R.string.global_privacy_value), "Medium")
            else -> itemView.privacyValue!!.text = SentenceAdapter.adapt(itemView.context.resources.getString(R.string.global_privacy_value), "High")
        }

        if (sensor.isSensor) {
            itemView.sensor_status_image!!.visibility = View.VISIBLE
            itemView.sensor_status_name!!.visibility = View.VISIBLE
            val isEnabled = sensor.isEnabled(itemView.context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemView.sensor_status_image!!.backgroundTintList = ColorStateList.valueOf(if (isEnabled) Color.RED else Color.GREEN)
            }
            itemView.sensor_status_name!!.text = if (isEnabled) "On" else "Off"
        } else {
            itemView.sensor_status_image!!.visibility = View.GONE
            itemView.sensor_status_name!!.visibility = View.GONE
        }
    }
}