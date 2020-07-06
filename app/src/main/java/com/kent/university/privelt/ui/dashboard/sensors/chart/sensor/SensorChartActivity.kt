/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart.sensor

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat
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
import com.google.android.material.snackbar.Snackbar
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
import kotlin.math.ceil
import kotlin.math.max
import kotlin.reflect.KMutableProperty

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
        ll.enableDashedLine(10f, 10f, 0f)
        ll.lineWidth = 0.2f
        return ll
    }

    private fun arrayListToPrimitiveArrayPermission(array: List<Float>): FloatArray {
        val res = FloatArray(array.size)
        for ((index, value) in array.withIndex())
            res[index] = value
        return res
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.help_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.help) {
            Snackbar.make(chart, R.string.if_a_color_is_transparent_the_sensor_were_not_activated, Snackbar.LENGTH_LONG).show()
            return true
        }
        return super.onOptionsItemSelected(item)
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

    private fun getMaximumAxisSensors(historyPermissions: List<HistoryPermission>): Int {
        var maximum = 0
        for (historyPermission in historyPermissions) {
            val kClass = Class.forName(historyPermission.javaClass.name).kotlin

            var total = 0
            for (member in kClass.members.filterIsInstance<KMutableProperty<*>>().filter { it.name.contains("Value") }) {
                total += member.getter.call(historyPermission) as Int
            }
            total += (100 - total % 100) + 20
            maximum = max(total, maximum)
        }
        return maximum
    }
    private fun setDataPermission(permissionStatus: List<HistoryPermission>, permissions: List<String>) {

        val values: ArrayList<BarEntry> = ArrayList()
        val dates: ArrayList<String> = ArrayList()

        for ((i, permissionS) in permissionStatus.withIndex()) {
            val tmpStack = ArrayList<Float>()
            for (sensor in Sensor.values()) {
                if (permissions.contains(sensor.title)) {

                    // Reflection to call each getter avoiding if forest

                    val kClass = Class.forName(permissionS.javaClass.name).kotlin

                    //Remove space and uppercase letters
                    val value = kClass.members.filterIsInstance<KMutableProperty<*>>().firstOrNull { it.name == sensor.title.toLowerCase(Locale.ROOT).replace(" ", "") + "Value" }
                    val sensorValue = kClass.members.filterIsInstance<KMutableProperty<*>>().firstOrNull { it.name == sensor.title.toLowerCase(Locale.ROOT).replace(" ", "") + "Sensor" }

                    val res = (value?.getter?.call(permissionS) as Int) + if (sensor.isSensor && !(sensorValue?.getter?.call(permissionS) as Boolean)) 0.5f else 0f
                    tmpStack.add(res)
                }
            }
            val b = BarEntry((i).toFloat(), arrayListToPrimitiveArrayPermission(tmpStack))
            dates.add(getDate(permissionS.date)!!)
            chart.xAxis.addLimitLine(getLimitLineAt(i))

            values.add(b)
        }

        chart.axisLeft.axisMaximum = getMaximumAxisSensors(permissionStatus).toFloat()

        val set1: BarDataSet

        set1 = BarDataSet(values, "Activated sensors")
        set1.setDrawIcons(false)

        val colors = ArrayList<Int>()
        for (sensor in Sensor.values())
            if (permissions.contains(sensor.title)) {
                colors.add(ResourcesCompat.getColor(resources, sensor.color, null))
            }
        set1.colors = colors

        drawChart(set1, dates)
    }

    private fun drawChart(set1: IBarDataSet, dates: ArrayList<String>) {
        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(set1)
        val data = BarData(dataSets)
        chart.data = data
        chart.data.setValueTextColor(Color.WHITE)
        chart.data.setDrawValues(true)
        chart.data.setValueTextSize(12f)
        chart.setDrawValueAboveBar(false)
        chart.data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toDouble() != ceil(value.toDouble())) "Off"
                else value.toInt().toString()
            }
        })

        chart.setFitBars(true)
        chart.xAxis.setLabelCount(7, false)
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
