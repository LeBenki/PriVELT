/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.kent.university.privelt.R
import com.kent.university.privelt.events.LaunchDetailedSensorEvent
import com.kent.university.privelt.model.Sensor
import org.greenrobot.eventbus.EventBus

internal class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    @BindView(R.id.image_sensor)
    var image: ImageView? = null

    @JvmField
    @BindView(R.id.sensor_value)
    var number: TextView? = null

    @JvmField
    @BindView(R.id.sensor_status_name)
    var statusName: TextView? = null

    @JvmField
    @BindView(R.id.sensor_status_image)
    var statusImage: View? = null
    fun bind(sensor: Sensor) {
        image!!.setImageResource(sensor.resId)
        number!!.text = sensor.getApplications().size.toString()
        itemView.setOnClickListener { view: View? -> EventBus.getDefault().post(LaunchDetailedSensorEvent(sensor)) }
        if (sensor.isSensor) {
            statusImage!!.visibility = View.VISIBLE
            statusName!!.visibility = View.VISIBLE
            val isEnabled = sensor.isEnabled(itemView.context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusImage!!.backgroundTintList = ColorStateList.valueOf(if (isEnabled) Color.RED else Color.GREEN)
            }
            statusName!!.text = if (isEnabled) "On" else "Off"
        } else {
            statusImage!!.visibility = View.GONE
            statusName!!.visibility = View.GONE
        }
    }

    init {
        ButterKnife.bind(this, itemView)
    }
}