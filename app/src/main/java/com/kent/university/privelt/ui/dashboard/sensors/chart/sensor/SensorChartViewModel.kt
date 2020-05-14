/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart.sensor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kent.university.privelt.model.PermissionStatus
import com.kent.university.privelt.model.SensorStatus
import com.kent.university.privelt.repositories.PermissionStatusRepository
import com.kent.university.privelt.repositories.SensorStatusRepository

class SensorChartViewModel(private val mSensorStatusRepository: SensorStatusRepository, private val mPermissionStatusRepository: PermissionStatusRepository) : ViewModel() {
    internal var sensorStatus: LiveData<List<SensorStatus>>? = null
    internal var permissionStatus: LiveData<List<PermissionStatus>>? = null

    fun init() {
        if (sensorStatus == null) sensorStatus = mSensorStatusRepository.sensorStatus
        if (permissionStatus == null) permissionStatus = mPermissionStatusRepository.permissionStatus
    }
}