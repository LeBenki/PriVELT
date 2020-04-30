/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors.detailed

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.model.Sensor
import com.kent.university.privelt.ui.dashboard.sensors.SensorFragment
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import kotlinx.android.synthetic.main.activity_detailed_sensor.*
import kotlinx.android.synthetic.main.activity_detailed_sensor.logo
import kotlinx.android.synthetic.main.activity_detailed_sensor.privacyValue
import kotlinx.android.synthetic.main.activity_detailed_sensor.risk_progress
import kotlinx.android.synthetic.main.activity_detailed_sensor.titleTV

class DetailedSensorActivity : BaseActivity() {
    private var sensor: Sensor? = null

    override val activityLayout: Int
        get() = R.layout.activity_detailed_sensor

    override fun configureViewModel() {}
    override fun configureDesign(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            sensor = savedInstanceState.getSerializable(SensorFragment.PARAM_SENSOR) as Sensor?
        } else if (intent.extras != null) {
            sensor = intent.extras!!.getSerializable(SensorFragment.PARAM_SENSOR) as Sensor?
        }
        logo!!.setImageResource(sensor!!.resId)
        titleTV!!.text = sensor!!.title
        setUpRecyclerView()

        var riskValue = sensor?.getApplications()?.size
        if (riskValue!! > 100) riskValue = 100
        when {
            riskValue < 20 -> privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Low")
            riskValue < 60 -> privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "Medium")
            else -> privacyValue!!.text = SentenceAdapter.adapt(resources.getString(R.string.global_privacy_value), "High")
        }
        risk_progress!!.progress = riskValue
    }

    private fun setUpRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        applications!!.layoutManager = layoutManager
        val applicationsAdapter = DetailedSensorAdapter(sensor!!.getApplications())
        applications!!.adapter = applicationsAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SensorFragment.PARAM_SENSOR, sensor)
    }
}