/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart.global

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.model.Sensor
import kotlinx.android.synthetic.main.activity_risk_value.*
import java.util.*

class RadarChartSensorActivity : BaseActivity() {

    override fun configureDesign(savedInstanceState: Bundle?) {
        chart!!.visibility = View.GONE
        no_data!!.visibility = View.VISIBLE
        val sensors = Sensor.values()
        configureChart(sensors)
    }

    override val activityLayout: Int
        get() = R.layout.activity_risk_value

    override fun configureViewModel() {
    }

    private fun configureChart(sensors: Array<Sensor>) {
        chart!!.visibility = View.VISIBLE
        no_data!!.visibility = View.GONE
        chart!!.description.isEnabled = false
        chart!!.webLineWidth = 1f
        chart!!.webColor = Color.LTGRAY
        chart!!.webLineWidthInner = 1f
        chart!!.webColorInner = Color.LTGRAY
        chart!!.webAlpha = 100

        setData(sensors)
        chart!!.animateXY(1400, 1400, Easing.EaseInOutQuad)
        val xAxis = chart!!.xAxis
        xAxis.textSize = 9f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() < Sensor.values().size.toFloat()) sensors[value.toInt()].title else ""
            }
        }
    }

    private fun setData(sensors: Array<Sensor>) {
        val sets: MutableList<IRadarDataSet> = ArrayList()

        val entries = ArrayList<RadarEntry>()

        for (sensor: Sensor in sensors) {
            val `val` = sensor.getApplications().size
            entries.add(RadarEntry(`val`.toFloat()))
        }
        val set1 = RadarDataSet(entries, "Permissions")
        val rnd = Random()
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        set1.color = color
        set1.fillColor = color
        set1.setDrawFilled(true)
        set1.fillAlpha = 180
        set1.lineWidth = 2f
        set1.isDrawHighlightCircleEnabled = true
        set1.setDrawHighlightIndicators(false)
        sets.add(set1)
        val data = RadarData(sets)
        data.setValueTextSize(15f)
        data.setDrawValues(false)
        data.setValueTextColor(Color.RED)
        val yAxis = chart!!.yAxis
        yAxis.setLabelCount(1, false)
        yAxis.textSize = 9f
        yAxis.axisMinimum = 0f

        yAxis.axisMaximum = sensors.maxBy { it.getApplications().size }?.getApplications()?.size?.toFloat()!!
        yAxis.setDrawLabels(true)
        val l = chart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 5f
        chart!!.data = data
        chart!!.invalidate()
    }
}