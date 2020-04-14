/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.risk_value

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import kotlinx.android.synthetic.main.activity_risk_value.*
import java.util.*

class RiskValueActivity : BaseActivity() {

    private var riskValueViewModel: RiskValueViewModel? = null
    private var services: List<Service>? = null
    private var userDatas: List<UserData>? = null
    private var type: String? = null
    private var service: String? = null
    private var isDataCentric = false

    override fun configureDesign(savedInstanceState: Bundle?) {
        services = ArrayList()
        userDatas = ArrayList()
        if (savedInstanceState != null) {
            type = savedInstanceState.getString(PARAM_DATA)
            service = savedInstanceState.getString(PARAM_SERVICE)
        } else if (intent != null) {
            type = intent.getStringExtra(PARAM_DATA)
            service = intent.getStringExtra(PARAM_SERVICE)
        }
        isDataCentric = !(type == null || type!!.isEmpty())
        chart!!.visibility = View.GONE
        no_data!!.visibility = View.VISIBLE
        getServices()
        userdatas
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PARAM_SERVICE, service)
        outState.putString(PARAM_DATA, type)
    }

    override val activityLayout: Int
        get() = R.layout.activity_risk_value

    override fun configureViewModel() {
        riskValueViewModel = getViewModel(RiskValueViewModel::class.java)
        riskValueViewModel?.init()
    }

    private fun updateUserDatas(userData: List<UserData>) {
        userDatas = userData
        if (userDatas!!.isNotEmpty() && services!!.isNotEmpty()) configureChart()
    }

    private fun updateServices(services: List<Service>) {
        this.services = services
        if (userDatas!!.isNotEmpty() && services.isNotEmpty()) configureChart()
    }

    private fun getServices() {
        riskValueViewModel!!.services!!.observe(this, Observer { services: List<Service> -> updateServices(services) })
    }

    private val userdatas: Unit
        get() {
            riskValueViewModel!!.userDatas?.observe(this, Observer { userData: List<UserData> -> updateUserDatas(userData) })
        }

    private fun configureChart() {
        chart!!.visibility = View.VISIBLE
        no_data!!.visibility = View.GONE
        chart!!.description.isEnabled = false
        chart!!.webLineWidth = 1f
        chart!!.webColor = Color.LTGRAY
        chart!!.webLineWidthInner = 1f
        chart!!.webColorInner = Color.LTGRAY
        chart!!.webAlpha = 100

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        val mv: MarkerView = RadarMarkerView(this, R.layout.radar_markerview)
        mv.chartView = chart // For bounds control
        chart!!.marker = mv // Set the marker to the chart
        val mActivities = numberOfTypes
        setData(mActivities)
        chart!!.animateXY(1400, 1400, Easing.EaseInOutQuad)
        val xAxis = chart!!.xAxis
        xAxis.textSize = 9f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (!isDataCentric) mActivities[value.toInt() % mActivities.size] else services!![value.toInt() % services!!.size].name
            }
        }
        //xAxis.setTextColor(Color.WHITE);
        //l.setTextColor(Color.WHITE);
    }

    private val numberOfTypes: Array<String>
        get() {
            val types: MutableSet<String> = HashSet()
            for ((_, type1) in userDatas!!) {
                types.add(type1)
            }
            return types.toTypedArray()
        }

    private fun setData(mActivities: Array<String>) {
        val sets: MutableList<IRadarDataSet> = ArrayList()

        // Looking if we draw a data centric graph or a service centric graph
        if (!isDataCentric) {
            for (service in services!!) {
                if (service.name == this.service || this.service == null || this.service!!.isEmpty()) {
                    val set1 = RadarDataSet(getDataEntriesForEachService(mActivities, service), service.name)
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
                }
            }
        } else {
            for (type in mActivities) {
                if (type == this.type || this.type == null || this.type!!.isEmpty()) {
                    val set1 = RadarDataSet(getDataEntriesForEachType(type, services), type)
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
                }
            }
        }
        val data = RadarData(sets)
        data.setValueTextSize(15f)
        data.setDrawValues(false)
        data.setValueTextColor(Color.RED)
        val yAxis = chart!!.yAxis
        if (!isDataCentric) yAxis.setLabelCount(mActivities.size, false) else yAxis.setLabelCount(1, false)
        yAxis.textSize = 9f
        yAxis.axisMinimum = 0f

        //TODO: 200 HARDCODED (MAX DATA)
        yAxis.axisMaximum = getMaximumValue(sets)
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

    private fun getMaximumValue(sets: List<IRadarDataSet>): Float {
        var max = 0f
        for (set in sets) {
            for (i in 0 until set.entryCount) {
                if (max < set.getEntryForIndex(i).value) {
                    max = set.getEntryForIndex(i).value
                }
            }
        }
        return max
    }

    private fun getDataEntriesForEachService(mActivities: Array<String>, service: Service): List<RadarEntry> {
        val entries = ArrayList<RadarEntry>()
        for (mActivity in mActivities) {
            val `val` = countTypeForEachService(mActivity, service) + 1
            entries.add(RadarEntry(`val`.toFloat()))
        }
        return entries
    }

    private fun getDataEntriesForEachType(type: String, services: List<Service>?): List<RadarEntry> {
        val entries = ArrayList<RadarEntry>()
        for (service in services!!) {
            val `val` = countServiceForEachType(type, service) + 1
            entries.add(RadarEntry(`val`.toFloat()))
        }
        return entries
    }

    private fun countTypeForEachService(mActivity: String, service: Service): Int {
        var count = 0
        for ((_, type1, _, _, serviceId) in userDatas!!) {
            if (serviceId == service.id && type1 == mActivity) count += 1
        }
        return count
    }

    private fun countServiceForEachType(mActivity: String, service: Service): Int {
        var count = 0
        for ((_, type1, _, _, serviceId) in userDatas!!) {
            if (serviceId == service.id && type1 == mActivity && mActivity == type1) count += 1
        }
        return count
    }

    companion object {
        const val PARAM_SERVICE = "service"
        const val PARAM_DATA = "data"
    }
}