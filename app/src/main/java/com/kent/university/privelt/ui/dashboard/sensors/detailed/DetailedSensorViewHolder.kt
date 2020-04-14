/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors.detailed

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.model.Application
import kotlinx.android.synthetic.main.cell_detailed_sensor.view.*

internal class DetailedSensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(application: Application) {
        itemView.title!!.text = application.name
    }

}