/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart.sensor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.ui.dashboard.sensors.chart.sensor.SensorChartFragment.Companion.PARAM_PERMISSION
import kotlinx.android.synthetic.main.activity_sensor_chart.*
import kotlinx.android.synthetic.main.fragment_sensor_chart.*


class SensorPagerActivity : BaseActivity(){

    private var permission: String? = null
    private var mSensorPagerAdapter: SensorPagerAdapter? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.help_menu, menu)
        return true
    }

    override val activityLayout: Int
        get() = R.layout.activity_sensor_chart

    override fun configureViewModel() {

    }

    override fun configureDesign(savedInstanceState: Bundle?) {

        if (savedInstanceState != null) {
            permission = savedInstanceState.getString(PARAM_PERMISSION)
        } else if (intent != null) {
            permission = intent.getStringExtra(PARAM_PERMISSION)
        }

        mSensorPagerAdapter = SensorPagerAdapter(supportFragmentManager, permission!!)
        sensorPager.adapter = mSensorPagerAdapter

        indicator.setViewPager(sensorPager)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PARAM_PERMISSION, permission)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.help) {
            Snackbar.make(chart, R.string.sensor_off_info, Snackbar.LENGTH_LONG).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}