/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart.sensor

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseFragment
import com.kent.university.privelt.events.CheckedSensorEvent
import com.kent.university.privelt.model.HistoryPermission
import com.kent.university.privelt.model.Sensor
import com.kent.university.privelt.ui.dashboard.sensors.chart.SensorChartAdapter
import com.kent.university.privelt.utils.sensors.SensorHelper.getNumberOfApplicationInstalled
import kotlinx.android.synthetic.main.fragment_sensor_chart.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.max
import kotlin.reflect.KMutableProperty

class SensorChartFragment : BaseFragment() {

    private var permission: String? = null
    private var position: Int? = null
    private var adapter: SensorChartAdapter? = null
    private var listPermissionStatus: List<HistoryPermission>? = null
    private var sensorsWithPosition: List<Sensor>? = null

    companion object {
        const val PARAM_PERMISSION = "permission"
        const val PARAM_POSITION = "position"

        fun newInstance(permission: String, position: Int): SensorChartFragment {
            val fragment = SensorChartFragment()
            val favoriteNeighbour = Bundle()
            favoriteNeighbour.putString(PARAM_PERMISSION, permission)
            favoriteNeighbour.putInt(PARAM_POSITION, position)
            fragment.arguments = favoriteNeighbour
            return fragment
        }
    }

    private var sensorStatusViewModel: SensorChartViewModel? = null

    override val fragmentLayout: Int
        get() = R.layout.fragment_sensor_chart

    override fun configureViewModel() {
        //Can not use it in this activity
    }

    private fun configureRecyclerView() {
        baseView.listSensor!!.layoutManager = GridLayoutManager(context, 2)
        val listSensors = ArrayList<Sensor>()
        sensorsWithPosition?.forEach {
            listSensors.add(it)
        }
        adapter = SensorChartAdapter(listSensors, listOf(permission!!))
        baseView.listSensor!!.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permission = arguments!!.getString(PARAM_PERMISSION)
        position = arguments!!.getInt(PARAM_POSITION)
    }

    override fun configureDesign(view: View) {

        sensorsWithPosition = getSensorWithPosition(position!!, SensorPagerAdapter.PAGE_SIZE)

        sensorStatusViewModel = getViewModel(SensorChartViewModel::class.java)

        sensorStatusViewModel?.init()

        sensorStatusViewModel?.permissionStatus?.observe(this, androidx.lifecycle.Observer { list ->
            Log.d("LUCAS", adapter?.getSelectedSensors()!!.isNotEmpty().toString())
            if (list.isNotEmpty() && adapter?.getSelectedSensors()!!.isNotEmpty())
                setDataPermission(list, adapter?.getSelectedSensors()!!)
            listPermissionStatus = list
        })

        view.numberOfApplications.text = getString(R.string.number_app, getNumberOfApplicationInstalled(context!!))
        view.chart.description.isEnabled = false

        // enable touch gestures
        view.chart.setTouchEnabled(true)

        view.chart.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging
        view.chart.isDragEnabled = true
        view.chart.setScaleEnabled(false)
        view.chart.setDrawGridBackground(false)
        view.chart.isHighlightPerDragEnabled = false
        view.chart.isHighlightFullBarEnabled = true

        view.chart.xAxis.setDrawLimitLinesBehindData(true)

        // set an alternative background color
        view.chart.setBackgroundColor(Color.WHITE)
        view.chart.setViewPortOffsets(0f, 0f, 0f, 0f)

        val xAxis = view.chart.xAxis
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
        view.chart.axisLeft.granularity = 1f
        val leftAxis = view.chart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.textColor = Color.rgb(0, 0, 0)
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.axisMinimum = 0f
        leftAxis.yOffset = -9f

        val rightAxis = view.chart.axisRight
        rightAxis.isEnabled = false

        val legend: Legend = view.chart.legend
        legend.isEnabled = false
        configureRecyclerView()
    }

    private fun getSensorWithPosition(position: Int, pageSize: Int): List<Sensor>? {
        val sensorList = ArrayList<Sensor>()
        for ((i, sensor) in Sensor.values().withIndex()) {
            if (i >= (position * pageSize) && i < ((position + 1) * pageSize)) {
                sensorList.add(sensor)
            }
        }
        return sensorList
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
                for (sensor in sensorsWithPosition!!) {
                    if (member.name.contains(sensor.name.toLowerCase().replace(" " , "")))
                        total += member.getter.call(historyPermission) as Int
                }
            }
            total += (100 - total % 100)
            maximum = max(total, maximum)
        }
        return maximum
    }
    private fun setDataPermission(permissionStatus: List<HistoryPermission>, permissions: List<String>) {

        val values: ArrayList<BarEntry> = ArrayList()
        val dates: ArrayList<String> = ArrayList()

        for ((i, permissionS) in permissionStatus.withIndex()) {
            val tmpStack = ArrayList<Float>()
            for (sensor in sensorsWithPosition!!) {
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
            view?.chart?.xAxis?.addLimitLine(getLimitLineAt(i))

            values.add(b)
        }

        view?.chart?.axisLeft?.axisMaximum = getMaximumAxisSensors(permissionStatus).toFloat()

        val set1: BarDataSet

        set1 = BarDataSet(values, "Activated sensors")
        set1.setDrawIcons(false)

        val colors = ArrayList<Int>()
        for (sensor in sensorsWithPosition!!)
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
        view?.chart?.data = data
        view?.chart?.data?.setValueTextColor(Color.WHITE)
        view?.chart?.data?.setDrawValues(true)
        view?.chart?.data?.setValueTextSize(12f)
        view?.chart?.setDrawValueAboveBar(false)
        view?.chart?.data?.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value == 0f)
                    return ""
                return if (value.toDouble() != ceil(value.toDouble())) "Off"
                else value.toInt().toString()
            }
        })

        view?.chart?.setFitBars(true)
        view?.chart?.xAxis?.setLabelCount(7, false)
        view?.chart?.moveViewToX(set1.entryCount.toFloat())
        view?.chart?.xAxis?.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() + 1 < dates.size) dates[value.toInt() + 1] else ""
            }
        }

        view?.chart?.data?.isHighlightEnabled = false
        view?.chart?.invalidate()
        view?.chart?.setVisibleXRangeMaximum(7f)
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
        if (adapter?.getSelectedSensors()!!.isNotEmpty())
            setDataPermission(listPermissionStatus!!, adapter?.getSelectedSensors()!!)
    }
}
