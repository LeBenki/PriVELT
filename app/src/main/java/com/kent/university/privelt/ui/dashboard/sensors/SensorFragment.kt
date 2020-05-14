/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseFragment
import com.kent.university.privelt.events.LaunchDetailedSensorEvent
import com.kent.university.privelt.model.Sensor
import com.kent.university.privelt.ui.dashboard.sensors.chart.global.RadarChartSensorActivity
import com.kent.university.privelt.ui.dashboard.sensors.detailed.DetailedSensorActivity
import com.kent.university.privelt.utils.sensors.SensorHelper
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import kotlinx.android.synthetic.main.fragment_sensors.view.*
import kotlinx.android.synthetic.main.header_card_risk.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class SensorFragment : BaseFragment() {

    override val fragmentLayout: Int
        get() = R.layout.fragment_sensors
    
    override fun configureViewModel() {}

    override fun configureDesign(view: View) {
        val sensorsList = SensorHelper.getSensorsInformation(context!!)

        setUpRecyclerView(sensorsList)
        updateOverallRiskValue(sensorsList)
    }

    private fun updateOverallRiskValue(sensorsList: List<Sensor>) {

        var riskValue = 0

        for (sensor: Sensor in sensorsList)
            riskValue += sensor.getApplications().size

        riskValue /= 3
        if (riskValue > 100) riskValue = 100
        when {
            riskValue < 20 -> baseView.privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Low")
            riskValue < 60 -> baseView.privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Medium")
            else -> baseView.privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "High")
        }
        baseView.progressBar!!.progress = riskValue
        baseView.progressBar.setOnClickListener {
            startActivity(Intent(activity, RadarChartSensorActivity::class.java))
        }
    }

    private fun setUpRecyclerView(sensorsList: List<Sensor>) {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        baseView.sensors?.layoutManager = layoutManager
        val cardAdapter = SensorAdapter(sensorsList)
        baseView.sensors?.adapter = cardAdapter
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
    fun onDetailedSensorEvent(event: LaunchDetailedSensorEvent) {
        val intent = Intent(activity, DetailedSensorActivity::class.java)
        intent.putExtra(PARAM_SENSOR, event.sensor)
        startActivity(intent)
    }

    companion object {
        const val PARAM_SENSOR = "PARAM_SENSOR"
    }
}