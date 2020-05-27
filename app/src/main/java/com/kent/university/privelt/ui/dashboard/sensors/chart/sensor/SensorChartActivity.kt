/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart.sensor

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.StackedValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.events.CheckedSensorEvent
import com.kent.university.privelt.model.PermissionStatus
import com.kent.university.privelt.model.Sensor
import com.kent.university.privelt.model.SensorStatus
import com.kent.university.privelt.ui.dashboard.sensors.chart.SensorChartAdapter
import kotlinx.android.synthetic.main.activity_sensor_chart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class SensorChartActivity : BaseActivity() {

    private var sensor: String? = null
    private var permission: String? = null
    private var adapter: SensorChartAdapter? = null
    private var listSensorStatus: List<SensorStatus>? = null
    private var listPermissionStatus: List<PermissionStatus>? = null
    private var isSensor: Boolean? = null

    companion object {
        const val PARAM_PERMISSION = "permission"
        const val PARAM_SENSOR = "sensor"
    }

    private var sensorStatusViewModel: SensorChartViewModel? = null

    override val activityLayout: Int
        get() = R.layout.activity_sensor_chart

    override fun configureViewModel() {
        //Can not use it in this activity
    }

    private fun configureRecyclerView() {
        listSensor!!.layoutManager = GridLayoutManager(this, 2)
        val listSensors = ArrayList<Sensor>()
        Sensor.values().forEach {
            if (it.isSensor || !isSensor!!)
                listSensors.add(it)
        }
        adapter = if (isSensor!!)
            SensorChartAdapter(listSensors, listOf(sensor!!))
        else
            SensorChartAdapter(listSensors, listOf(permission!!))
        listSensor!!.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PARAM_SENSOR, sensor)
        outState.putString(PARAM_PERMISSION, permission)
    }

    override fun configureDesign(savedInstanceState: Bundle?) {

        if (savedInstanceState != null) {
            sensor = savedInstanceState.getString(PARAM_SENSOR)
            permission = savedInstanceState.getString(PARAM_PERMISSION)
        } else if (intent != null) {
            sensor = intent.getStringExtra(PARAM_SENSOR)
            permission = intent.getStringExtra(PARAM_PERMISSION)

        }

        isSensor = sensor != null && sensor!!.isNotEmpty()

        sensorStatusViewModel = getViewModel(SensorChartViewModel::class.java)

        sensorStatusViewModel?.init(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3, System.currentTimeMillis())

        sensorStatusViewModel?.sensorStatus?.observe(this, androidx.lifecycle.Observer { list ->
            if (isSensor!!)
                setDataSensor(list, adapter?.getSelectedSensors()!!)
            listSensorStatus = list
        })
        sensorStatusViewModel?.permissionStatus?.observe(this, androidx.lifecycle.Observer { list ->
            if (!isSensor!!)
                setDataPermission(list, adapter?.getSelectedSensors()!!)
            listPermissionStatus = list
        })

        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(true)

        chart.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = false
        chart.isHighlightFullBarEnabled = true

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE)
        chart.setViewPortOffsets(0f, 0f, 0f, 0f)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.textSize = 10f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.textColor = Color.rgb(0, 0, 0)
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 0.01f // one hour

        xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat = SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH)
            override fun getFormattedValue(value: Float): String {
                val millis: Long = TimeUnit.HOURS.toMillis(value.toLong())
                return mFormat.format(Date(millis))
            }
        }

        val leftAxis = chart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.textColor = Color.rgb(0, 0, 0)
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.axisMinimum = 0f
        //TODO: hardcoded
        leftAxis.axisMaximum = if (isSensor!!) Sensor.values().filter { it.isSensor }.size.toFloat() else 500f
        leftAxis.yOffset = -9f

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false

        chart.legend.isEnabled = false
        val mv = MyMarkerView(this, R.layout.custom_marker_view)
        if (!isSensor!!)
            chart.marker = mv
        configureRecyclerView()
    }

    private fun arrayListToPrimitiveArraySensor(array: List<SensorStatus>): FloatArray {
        val res = FloatArray(array.size)
        for ((index, value) in array.withIndex())
            res[index] = if (value.wereActivated) 1f else 0f
        return res
    }

    private fun arrayListToPrimitiveArrayPermission(array: List<Float>): FloatArray {
        val res = FloatArray(array.size)
        for ((index, value) in array.withIndex())
            res[index] = value
        return res
    }

    private fun setDataPermission(permissionStatus: List<PermissionStatus>, permissions: List<String>) {

        val values: ArrayList<BarEntry> = ArrayList()

        for (permissionS in permissionStatus.distinctBy { it.date }) {
            val tmpStack = ArrayList<Float>()
            val tmpSensors = ArrayList<String>()
            for (sensor in Sensor.values()) {
                if (permissions.contains(sensor.title)) {

                    val resForSensor = permissionStatus.filter { it.date == permissionS.date && it.permissionName.toUpperCase(Locale.ROOT).contains(sensor.title.toUpperCase(Locale.ROOT)) }

                    val tmpSensorState = listSensorStatus?.filter { it.sensorName == sensor.title && it.date == permissionS.date }
                    if (sensor.isSensor) {
                        if (tmpSensorState != null && tmpSensorState.isNotEmpty())
                            tmpSensors.add(sensor.title + ": " + if (tmpSensorState[0].wereActivated) "activated" else "unactivated")
                        else
                            tmpSensors.add(sensor.title + ": " + "unknown")
                    }
                    tmpStack.add(resForSensor.size.toFloat())
                }
            }
            val b = BarEntry(TimeUnit.MILLISECONDS.toHours(permissionS.date).toFloat(), arrayListToPrimitiveArrayPermission(tmpStack))
            b.data = tmpSensors
            values.add(b)
        }

        val distValues = values.distinctBy { it.x }
        val set1: BarDataSet

        set1 = BarDataSet(distValues, "Activated sensors")
        set1.setDrawIcons(false)

        val colors = ArrayList<Int>()
        for (sensor in Sensor.values())
            if (permissions.contains(sensor.title))
            colors.add(ResourcesCompat.getColor(resources, sensor.color, null))
        set1.colors = colors

        drawChart(set1)
    }

    private fun setDataSensor(sensorStatus: List<SensorStatus>, sensors: List<String>) {

        val values: ArrayList<BarEntry> = ArrayList()

        for (sensorS in sensorStatus.distinctBy { it.date }) {
            val res = sensorStatus.filter { it.date == sensorS.date && sensors.contains(it.sensorName) }
            values.add(BarEntry(TimeUnit.MILLISECONDS.toHours(sensorS.date).toFloat(), arrayListToPrimitiveArraySensor(res), "bite"))
        }

        val distValues = values.distinctBy { it.x }
        val set1: BarDataSet

        set1 = BarDataSet(distValues, "Activated sensors")
        set1.setDrawIcons(false)
        val colors = ArrayList<Int>()
        for (sensor in Sensor.values())
            if (sensors.contains(sensor.title))
                colors.add(ResourcesCompat.getColor(resources, sensor.color, null))
        set1.colors = colors

        drawChart(set1)
    }

    private fun drawChart(set1: IBarDataSet) {
        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(set1)
        val data = BarData(dataSets)
        data.setValueFormatter(StackedValueFormatter(false, "", 1))
        data.setValueTextColor(Color.WHITE)
        chart.data = data

        chart.setFitBars(true)
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
        if (isSensor!!)
            setDataSensor(listSensorStatus!!, adapter?.getSelectedSensors()!!)
        else
            setDataPermission(listPermissionStatus!!, adapter?.getSelectedSensors()!!)
    }
}
