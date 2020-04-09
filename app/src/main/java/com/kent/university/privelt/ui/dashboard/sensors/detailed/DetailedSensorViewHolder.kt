/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors.detailed

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.kent.university.privelt.R
import com.kent.university.privelt.model.Application

internal class DetailedSensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    @BindView(R.id.title)
    var title: TextView? = null
    fun bind(application: Application) {
        title!!.text = application.name
    }

    init {
        ButterKnife.bind(this, itemView)
    }
}