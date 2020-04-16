/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors.detailed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.model.Application

internal class DetailedSensorAdapter(private val applicationList: List<Application>) : RecyclerView.Adapter<DetailedSensorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailedSensorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_detailed_sensor, parent, false)
        return DetailedSensorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailedSensorViewHolder, position: Int) {
        holder.bind(applicationList[position])
    }

    override fun getItemCount(): Int {
        return applicationList.size
    }

}