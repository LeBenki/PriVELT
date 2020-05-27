/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors.chart.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.kent.university.privelt.R

@SuppressLint("ViewConstructor")
@Suppress("UNCHECKED_CAST")
class MyMarkerView(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {
    private val tvContent: TextView = findViewById<View>(R.id.tvContent) as TextView

    override fun refreshContent(e: Entry, highlight: Highlight) {
        val s: ArrayList<String> = e.data as ArrayList<String>
        tvContent.text = ""
        for ((i, text) in s.withIndex()) {
            tvContent.append(text)
            if (i % 2 == 1)
                tvContent.append("\n")
            else if (i < s.size - 1)
                tvContent.append(", ")
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

}