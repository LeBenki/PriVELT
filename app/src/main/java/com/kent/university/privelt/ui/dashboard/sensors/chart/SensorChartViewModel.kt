/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kent.university.privelt.model.SensorStatus
import com.kent.university.privelt.repositories.SensorStatusRepository

class SensorChartViewModel(private val mSensorStatusRepository: SensorStatusRepository) : ViewModel() {
    private var sensorStatus: LiveData<List<SensorStatus>>? = null

    fun init() {
        if (sensorStatus == null) sensorStatus = mSensorStatusRepository.sensorStatus
    }

    fun getSensorStatus() : LiveData<List<SensorStatus>>? {
        return sensorStatus
    }
}