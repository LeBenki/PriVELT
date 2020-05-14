/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors.chart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.model.Sensor
import java.util.*
import kotlin.collections.ArrayList

class SensorChartAdapter internal constructor(private val scripts: ArrayList<Sensor>, alreadyChecked: List<String>) : RecyclerView.Adapter<SensorChartViewHolder>() {
    private val sensors: LinkedHashMap<String?, Boolean> = LinkedHashMap()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorChartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_sensor_checkbox, parent, false)
        return SensorChartViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorChartViewHolder, position: Int) {
        holder.bind(scripts[position], sensors)
    }

    override fun getItemCount(): Int {
        return sensors.size
    }

    fun getSelectedSensors(): List<String> {
        val list = ArrayList<String>()
        sensors.forEach {
            if (it.value)
                list.add(it.key!!)
        }
        return list
    }

    init {
        for (script in scripts) {
            this.sensors[script.title] = false
        }
        for (script in alreadyChecked) {
            if (script.isNotEmpty() && this.sensors.containsKey(script))
                this.sensors[script] = true
        }
    }
}