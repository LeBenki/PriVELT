/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart.sensor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.kent.university.privelt.model.Sensor


class SensorPagerAdapter(fm: FragmentManager?, private val permission: String) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /**
     * getItem is called to instantiate the fragment for the given page.
     * @param position
     * @return
     */
    override fun getItem(position: Int): Fragment {
        return SensorChartFragment.newInstance(permission, position)
    }

    /**
     * get the number of pages
     * @return
     */
    override fun getCount(): Int {
        val addOneIfNecessary = if (Sensor.values().size % PAGE_SIZE != 0) 1 else 0
        return (Sensor.values().size / PAGE_SIZE) + addOneIfNecessary //Number of sensor per page
    }

    companion object {
        const val PAGE_SIZE = 8
    }
}