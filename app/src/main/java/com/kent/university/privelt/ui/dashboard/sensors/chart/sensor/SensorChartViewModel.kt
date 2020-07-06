/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.chart.sensor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kent.university.privelt.model.HistoryPermission
import com.kent.university.privelt.repositories.HistoryPermissionRepository

class SensorChartViewModel(private val mHistoryPermissionRepository: HistoryPermissionRepository) : ViewModel() {
    internal var permissionStatus: LiveData<List<HistoryPermission>>? = null

    fun init() {
        if (permissionStatus == null) permissionStatus = mHistoryPermissionRepository.getPermissions()
    }
}