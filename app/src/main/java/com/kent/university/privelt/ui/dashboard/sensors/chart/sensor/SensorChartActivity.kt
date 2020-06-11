/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart.sensor

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.events.CheckedSensorEvent
import com.kent.university.privelt.model.HistoryPermission
import com.kent.university.privelt.model.Sensor
import com.kent.university.privelt.ui.dashboard.sensors.chart.SensorChartAdapter
import com.kent.university.privelt.utils.sensors.SensorHelper.getNumberOfApplicationInstalled
import kotlinx.android.synthetic.main.activity_sensor_chart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SensorChartActivity : BaseActivity() {

    private var permission: String? = null
    private var adapter: SensorChartAdapter? = null
    private var listPermissionStatus: List<HistoryPermission>? = null

    companion object {
        const val PARAM_PERMISSION = "permission"
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
            listSensors.add(it)
        }
        adapter = SensorChartAdapter(listSensors, listOf(permission!!))
        listSensor!!.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PARAM_PERMISSION, permission)
    }

    override fun configureDesign(savedInstanceState: Bundle?) {

        if (savedInstanceState != null) {
            permission = savedInstanceState.getString(PARAM_PERMISSION)
        } else if (intent != null) {
            permission = intent.getStringExtra(PARAM_PERMISSION)

        }

        sensorStatusViewModel = getViewModel(SensorChartViewModel::class.java)

        sensorStatusViewModel?.init()

        sensorStatusViewModel?.permissionStatus?.observe(this, androidx.lifecycle.Observer { list ->
            if (list.isNotEmpty())
                setDataPermission(list, adapter?.getSelectedSensors()!!)
            listPermissionStatus = list
        })

        numberOfApplications.text = getString(R.string.number_app, getNumberOfApplicationInstalled(this))
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(true)

        chart.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(false)
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = false
        chart.isHighlightFullBarEnabled = true

        chart.xAxis.setDrawLimitLinesBehindData(true)

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE)
        chart.setViewPortOffsets(0f, 0f, 0f, 0f)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.textSize = 10f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(true)
        xAxis.textColor = Color.rgb(0, 0, 0)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setCenterAxisLabels(true)
        chart.axisLeft.granularity = 1f
        val leftAxis = chart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.textColor = Color.rgb(0, 0, 0)
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.axisMinimum = 0f
        //TODO: hardcoded
        leftAxis.axisMaximum = 500f
        leftAxis.yOffset = -9f

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false

        val legend: Legend = chart.legend
        legend.isEnabled = false
        configureRecyclerView()
    }

    private fun getLimitLineAt(xIndex: Int): LimitLine {
        val ll = LimitLine(xIndex.toFloat()) // set where the line should be drawn
        ll.lineColor = Color.BLACK
        ll.lineWidth = 0.5f
        return ll
    }

    private fun arrayListToPrimitiveArrayPermission(array: List<Float>): FloatArray {
        val res = FloatArray(array.size)
        for ((index, value) in array.withIndex())
            res[index] = value
        return res
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(milliSeconds: Long): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("dd/MM")

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private fun setDataPermission(permissionStatus: List<HistoryPermission>, permissions: List<String>) {

        val values: ArrayList<BarEntry> = ArrayList()
        val dates: ArrayList<String> = ArrayList()

        //TODO use reflection
        for ((i, permissionS) in permissionStatus.withIndex()) {
            val tmpStack = ArrayList<Float>()
            if (permissions.contains("Location")) {
                if (permissionS.locationSensor) {
                    tmpStack.add(permissionS.locationValue.toFloat())
                    tmpStack.add(0f)
                }
                else {
                    tmpStack.add(0f)
                    tmpStack.add(permissionS.locationValue.toFloat())
                }
            }
            if (permissions.contains("Bluetooth")) {
                if (permissionS.bluetoothSensor) {
                    tmpStack.add(permissionS.bluetoothValue.toFloat())
                    tmpStack.add(0f)
                }
                else {
                    tmpStack.add(0f)
                    tmpStack.add(permissionS.bluetoothValue.toFloat())
                }
            }
            if (permissions.contains("Storage"))
                tmpStack.add(permissionS.storageValue.toFloat())
            if (permissions.contains("WIFI")) {
                if (permissionS.wifiSensor) {
                    tmpStack.add(permissionS.wifiValue.toFloat())
                    tmpStack.add(0f)
                }
                else {
                    tmpStack.add(0f)
                    tmpStack.add(permissionS.wifiValue.toFloat())
                }
            }
            if (permissions.contains("NFC")) {
                if (permissionS.nfcSensor) {
                    tmpStack.add(permissionS.nfcValue.toFloat())
                    tmpStack.add(0f)
                }
                else {
                    tmpStack.add(0f)
                    tmpStack.add(permissionS.nfcValue.toFloat())
                }
            }
            if (permissions.contains("Contacts"))
                tmpStack.add(permissionS.contactsValue.toFloat())
            if (permissions.contains("Calendar"))
                tmpStack.add(permissionS.calendarValue.toFloat())
            if (permissions.contains("SMS"))
                tmpStack.add(permissionS.smsValue.toFloat())
            val b = BarEntry((i).toFloat(), arrayListToPrimitiveArrayPermission(tmpStack))
            dates.add(getDate(permissionS.date)!!)
            chart.xAxis.addLimitLine(getLimitLineAt(i))
            values.add(b)
        }

        val set1: BarDataSet

        set1 = BarDataSet(values, "Activated sensors")
        set1.setDrawIcons(false)

        val colors = ArrayList<Int>()
        for (sensor in Sensor.values())
            if (permissions.contains(sensor.title)) {
                colors.add(ResourcesCompat.getColor(resources, sensor.color, null))
                if (sensor.isSensor) {
                    var color = ResourcesCompat.getColor(resources, sensor.color, null)
                    color = ColorUtils.setAlphaComponent(color, 200)
                    colors.add(color)
                }
            }
        set1.colors = colors

        drawChart(set1, dates)
    }

    private fun drawChart(set1: IBarDataSet, dates: ArrayList<String>) {
        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(set1)
        val data = BarData(dataSets)
        data.setValueTextColor(Color.WHITE)
        chart.data = data
        chart.data.setValueTextColor(Color.BLACK)
        chart.data.setDrawValues(true)
        chart.setFitBars(true)
        chart.xAxis.setLabelCount(7, true)
        chart.moveViewToX(set1.entryCount.toFloat())
        chart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() + 1 < dates.size) dates[value.toInt() + 1] else ""
            }
        }

        chart.data.isHighlightEnabled = false
        chart.invalidate()
        chart.setVisibleXRangeMaximum(7f)
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
        setDataPermission(listPermissionStatus!!, adapter?.getSelectedSensors()!!)
    }
}
