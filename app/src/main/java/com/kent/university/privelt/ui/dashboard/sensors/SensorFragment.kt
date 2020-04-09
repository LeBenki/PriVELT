/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseFragment
import com.kent.university.privelt.events.LaunchDetailedSensorEvent
import com.kent.university.privelt.ui.dashboard.sensors.detailed.DetailedSensorActivity
import com.kent.university.privelt.utils.sensors.SensorHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class SensorFragment : BaseFragment() {
    @JvmField
    @BindView(R.id.sensors)
    var sensors: RecyclerView? = null
    override fun getFragmentLayout(): Int {
        return R.layout.fragment_sensors
    }

    override fun configureViewModel() {}
    override fun configureDesign() {
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val sensorsList = SensorHelper.getSensorsInformation(context)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        sensors!!.layoutManager = layoutManager
        val cardAdapter = SensorAdapter(sensorsList)
        sensors!!.adapter = cardAdapter
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