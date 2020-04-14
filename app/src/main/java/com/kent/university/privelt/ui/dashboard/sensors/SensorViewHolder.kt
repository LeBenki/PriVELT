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
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.events.LaunchDetailedSensorEvent
import com.kent.university.privelt.model.Sensor
import kotlinx.android.synthetic.main.cell_application.view.*
import org.greenrobot.eventbus.EventBus

internal class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(sensor: Sensor) {
        itemView.image_sensor!!.setImageResource(sensor.resId)
        itemView.sensor_value!!.text = sensor.getApplications().size.toString()
        itemView.setOnClickListener { EventBus.getDefault().post(LaunchDetailedSensorEvent(sensor)) }
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