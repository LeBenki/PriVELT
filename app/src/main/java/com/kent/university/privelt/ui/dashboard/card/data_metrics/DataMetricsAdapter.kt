/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card.data_metrics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.model.CardItem
import java.util.*

class DataMetricsAdapter : RecyclerView.Adapter<DataMetricsViewHolder>() {
    private var dataMetrics: List<CardItem>
    private var isService = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataMetricsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_metrics, parent, false)
        return DataMetricsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataMetricsViewHolder, position: Int) {
        holder.bind(dataMetrics[position], isService)
    }

    override fun getItemCount(): Int {
        return dataMetrics.size
    }

    fun setDataMetrics(dataMetrics: List<CardItem>, isService: Boolean) {
        this.dataMetrics = dataMetrics
        this.isService = isService
    }

    init {
        dataMetrics = ArrayList()
    }
}