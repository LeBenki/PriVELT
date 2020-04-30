/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.model.SensorStatus
import kotlinx.android.synthetic.main.activity_sensor_chart.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class SensorChartActivity : BaseActivity() {

    private var sensor: String? = null

    companion object {
        const val PARAM_SENSOR = "sensor"
    }

    private var sensorStatusViewModel: SensorChartViewModel? = null

    override val activityLayout: Int
        get() = R.layout.activity_sensor_chart

    override fun configureViewModel() {
        sensorStatusViewModel = getViewModel(SensorChartViewModel::class.java)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PARAM_SENSOR, sensor)
    }

    override fun configureDesign(savedInstanceState: Bundle?) {

        if (savedInstanceState != null) {
            sensor = savedInstanceState.getString(PARAM_SENSOR)
        } else if (intent != null) {
            sensor = intent.getStringExtra(PARAM_SENSOR)
        }

        sensorStatusViewModel?.init(sensor!!)
        sensorStatusViewModel?.getSensorStatusForName()?.observe(this, androidx.lifecycle.Observer { list -> setData(list) })
        chart.description.isEnabled = false

        // enable touch gestures

        // enable touch gestures
        chart.setTouchEnabled(true)

        chart.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = true

        // set an alternative background color

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE)
        chart.setViewPortOffsets(0f, 0f, 0f, 0f)

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l = chart.legend
        l.isEnabled = false

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.textSize = 10f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(true)
        xAxis.textColor = Color.rgb(0, 0, 0)
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f // one hour

        xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat = SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH)
            override fun getFormattedValue(value: Float): String {
                val millis: Long = TimeUnit.HOURS.toMillis(value.toLong())
                return mFormat.format(Date(millis))
            }
        }

        val yAxis = chart.axisLeft
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value == 1f) "Activated" else if (value == 0f) "Deactivated" else ""
            }
        }

        val leftAxis = chart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.axisMinimum = -1f
        leftAxis.axisMaximum = 2f
        leftAxis.yOffset = -9f

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
    }

    private fun setData(sensorStatus: List<SensorStatus>) {

        val values: ArrayList<Entry> = ArrayList()

        for (sensor: SensorStatus in sensorStatus) {
            values.add(Entry(TimeUnit.MILLISECONDS.toHours(sensor.date).toFloat(), if (sensor.wereActivated) 1f else 0f))
            Log.d("LUCAS", Entry(TimeUnit.MILLISECONDS.toHours(sensor.date).toFloat(), if (sensor.wereActivated) 1f else 0f).toString())
        }


        // create a dataset and give it a type

        // create a dataset and give it a type
        val set1 = LineDataSet(values, sensor)
        set1.axisDependency = AxisDependency.LEFT
        set1.color = ColorTemplate.getHoloBlue()
        set1.valueTextColor = ColorTemplate.getHoloBlue()
        set1.lineWidth = 1.5f
        set1.setDrawCircles(false)
        set1.setDrawValues(false)
        set1.fillAlpha = 65
        set1.fillColor = ColorTemplate.getHoloBlue()
        set1.highLightColor = Color.rgb(0, 0, 0)
        set1.setDrawCircleHole(false)

        // create a data object with the data sets

        // create a data object with the data sets
        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)

        // set data

        // set data
        chart.data = data
        chart.invalidate()
    }
}
