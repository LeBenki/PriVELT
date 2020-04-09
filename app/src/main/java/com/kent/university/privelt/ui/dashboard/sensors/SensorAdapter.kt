/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.model.Sensor

internal class SensorAdapter internal constructor(private val sensorList: List<Sensor>) : RecyclerView.Adapter<SensorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_application, parent, false)
        return SensorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        holder.bind(sensorList[position])
    }

    override fun getItemCount(): Int {
        return sensorList.size
    }

}