/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.risk_value

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import kotlinx.android.synthetic.main.activity_barchart.*
import java.util.*

class BarActivity : BaseActivity() {
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
            type = savedInstanceState.getString(RiskValueActivity.PARAM_DATA)
            service = savedInstanceState.getString(RiskValueActivity.PARAM_SERVICE)
        } else if (intent != null) {
            type = intent.getStringExtra(RiskValueActivity.PARAM_DATA)
            service = intent.getStringExtra(RiskValueActivity.PARAM_SERVICE)
        }
        isDataCentric = !(type == null || type!!.isEmpty())
        title = if (isDataCentric) SentenceAdapter.capitaliseFirstLetter(type) else service
        chart!!.description.isEnabled = false

        // scaling can now only be done on x- and y-axis separately
        chart!!.setPinchZoom(false)
        chart!!.setDrawBarShadow(false)
        chart!!.setDrawGridBackground(false)
        val xAxis = chart!!.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        chart!!.axisLeft.setDrawGridLines(false)

        // add a nice and smooth animation
        chart!!.animateY(1500)
        val l = chart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = Legend.LegendForm.SQUARE
        l.formSize = 9f
        l.textSize = 11f
        l.xEntrySpace = 4f
        configureViewModel()
        getServices()
        userdatas
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(RiskValueActivity.PARAM_SERVICE, service)
        outState.putString(RiskValueActivity.PARAM_DATA, type)
    }

    override val activityLayout: Int
        get() = R.layout.activity_barchart

    override fun configureViewModel() {
        riskValueViewModel = getViewModel(RiskValueViewModel::class.java)
        riskValueViewModel?.init()
    }

    private fun updateUserDatas(userData: List<UserData>) {
        userDatas = userData
        if (userDatas!!.isNotEmpty() && services!!.isNotEmpty()) loadData()
    }

    private fun updateServices(services: List<Service>) {
        this.services = services
        if (userDatas!!.isNotEmpty() && services.isNotEmpty()) loadData()
    }

    //TODO: refactor les requêtes SQL
    private fun getServices() {
        riskValueViewModel!!.services?.observe(this, Observer { services: List<Service> -> updateServices(services) })
    }

    private val userdatas: Unit
        get() {
            riskValueViewModel!!.userDatas?.observe(this, Observer { userData: List<UserData> -> updateUserDatas(userData) })
        }

    private val numberOfTypes: Array<String>
        get() {
            val types: MutableSet<String> = HashSet()
            for ((_, type1) in userDatas!!) {
                types.add(type1)
            }
            return types.toTypedArray()
        }

    private fun countTypeForEachService(mActivity: String, service: Service): Float {
        var count = 0F
        for ((_, type1, _, _, serviceId) in userDatas!!) {
            if (serviceId == service.id && type1 == mActivity) count += 1
        }
        return count
    }

    private fun countServiceForEachType(mActivity: String, service: Service): Float {
        var count = 0F
        for ((_, type1, _, _, serviceId) in userDatas!!) {
            if (serviceId == service.id && type1 == mActivity && mActivity == type1) count += 1
        }
        return count
    }

    private fun loadData() {
        val allValues: MutableList<ArrayList<BarEntry>> = ArrayList()
        val mActivities = numberOfTypes
        var i = 0F
        if (!isDataCentric) {
            for (service in services!!) {
                if (service.name == this.service) {
                    for (mActivity in mActivities) {
                        val value = countTypeForEachService(mActivity, service)
                        val values = ArrayList<BarEntry>()
                        values.add(BarEntry(i++, value))
                        allValues.add(values)
                    }
                    break
                }
            }
        } else {
            for (type in mActivities) {
                if (type == this.type) {
                    for (service in services!!) {
                        val value = countServiceForEachType(type, service)
                        val values = ArrayList<BarEntry>()
                        values.add(BarEntry(i++, value))
                        allValues.add(values)
                    }
                    break
                }
            }
        }
        val sets: MutableList<IBarDataSet> = ArrayList()
        for ((j, values) in allValues.withIndex()) {
            val set = BarDataSet(values, if (isDataCentric) services!![j].name else mActivities[j])
            val rnd = Random()
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            set.color = color
            sets.add(set)
        }
        val data = BarData(sets)
        data.setValueFormatter(LargeValueFormatter())
        chart!!.data = data
        val groupSpace = 0.08f
        val barSpace = 0.03f
        val barWidth = 0.4f
        chart!!.barData.barWidth = barWidth
        data.getGroupWidth(groupSpace, barSpace)
        chart!!.xAxis.axisMinimum = 0f
        chart!!.xAxis.axisMaximum = allValues.size / 2.toFloat()
        if (allValues.size > 1) chart!!.groupBars(0f, groupSpace, barSpace)
        chart!!.invalidate()
        chart!!.setFitBars(true)
        chart!!.invalidate()
    }
}