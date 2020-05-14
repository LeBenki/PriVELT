/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart

import android.content.res.ColorStateList
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.events.CheckedSensorEvent
import com.kent.university.privelt.model.Sensor
import kotlinx.android.synthetic.main.cell_sensor_checkbox.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class SensorChartViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(sensor: Sensor?, scripts: LinkedHashMap<String?, Boolean>) {
        itemView.sensorCheckbox.text = sensor?.title
        itemView.sensorCheckbox.isChecked = scripts[sensor?.title]!!
        itemView.sensorCheckbox.setTextColor(ResourcesCompat.getColor(itemView.context.resources, sensor?.color!!, null))
        CompoundButtonCompat.setButtonTintList(itemView.sensorCheckbox, ColorStateList.valueOf(ResourcesCompat.getColor(itemView.context.resources, sensor.color, null)))

        itemView.sensorCheckbox.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            scripts[sensor.title] = b
            EventBus.getDefault().post(CheckedSensorEvent())
        }

        itemView.logo.setImageResource(sensor.resId)
    }
}