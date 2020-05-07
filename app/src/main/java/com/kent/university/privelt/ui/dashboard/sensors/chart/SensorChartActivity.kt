/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart

import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.events.CheckedSensorEvent
import com.kent.university.privelt.model.Sensor
import com.kent.university.privelt.model.SensorStatus
import kotlinx.android.synthetic.main.activity_sensor_chart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class SensorChartActivity : BaseActivity() {

    private var sensor: String? = null
    private var adapter: SensorChartAdapter? = null
    private var listSensorStatus: List<SensorStatus>? = null

    companion object {
        const val PARAM_SENSOR = "sensor"
        val colors = intArrayOf(
                ColorTemplate.MATERIAL_COLORS[0],
                ColorTemplate.MATERIAL_COLORS[1],
                ColorTemplate.MATERIAL_COLORS[2],
                ColorTemplate.MATERIAL_COLORS[3]
        )
    }

    private var sensorStatusViewModel: SensorChartViewModel? = null

    override val activityLayout: Int
        get() = R.layout.activity_sensor_chart

    override fun configureViewModel() {
        sensorStatusViewModel = getViewModel(SensorChartViewModel::class.java)
    }

    private fun configureRecyclerView() {
        listSensor!!.layoutManager = GridLayoutManager(this, 2)
        val listSensors = ArrayList<String>()
        Sensor.values().forEach {
            if (it.isSensor)
                listSensors.add(it.title)
        }
        adapter = SensorChartAdapter(listSensors, listOf(sensor!!))
        listSensor!!.adapter = adapter
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

        sensorStatusViewModel?.init()
        sensorStatusViewModel?.getSensorStatus()?.observe(this, androidx.lifecycle.Observer {
            list -> setData(list, adapter?.getSelectedSensors()!!)
            listSensorStatus = list
        })
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
        leftAxis.axisMinimum = -0.5f
        leftAxis.axisMaximum = 1.5f
        leftAxis.yOffset = -9f

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false

        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)

        configureRecyclerView()
    }

    private fun setData(sensorStatus: List<SensorStatus>, sensors: List<String>) {

        val dataSets: ArrayList<ILineDataSet> = ArrayList()

        for ((i, sensor: String) in sensors.withIndex()) {
            val values: ArrayList<Entry> = ArrayList()

            for (sensorStat: SensorStatus in sensorStatus) {
                if (sensorStat.sensorName == sensor)
                    values.add(Entry(TimeUnit.MILLISECONDS.toHours(sensorStat.date).toFloat(), if (sensorStat.wereActivated) 1f else 0f))
            }

            // create a dataset and give it a type
            val set1 = LineDataSet(values, sensor)
            set1.axisDependency = AxisDependency.LEFT
            set1.color = colors[i]
            set1.valueTextColor = colors[i]
            set1.lineWidth = 1.5f
            set1.setDrawCircles(false)
            set1.setDrawValues(false)
            set1.fillAlpha = 65
            set1.fillColor = colors[i]
            set1.highLightColor = Color.rgb(0, 0, 0)
            set1.setDrawCircleHole(false)

            dataSets.add(set1)
        }
        // create a data object with the data sets

        // create a data object with the data sets
        val data = LineData(dataSets)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)

        // set data

        // set data
        chart.data = data
        chart.invalidate()
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onCheckedSensor(event: CheckedSensorEvent) {
       setData(listSensorStatus!!, adapter?.getSelectedSensors()!!)
    }

}
