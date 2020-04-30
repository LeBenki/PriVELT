/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.repositories

import androidx.lifecycle.LiveData
import com.kent.university.privelt.database.dao.SensorStatusDao
import com.kent.university.privelt.model.SensorStatus
import javax.inject.Inject

class SensorStatusRepository @Inject constructor(private val mSensorStatusDao: SensorStatusDao) {
    val sensorStatus: LiveData<List<SensorStatus>>
        get() = mSensorStatusDao.sensorStatus!!

    fun getSensorStatusForName(name: String) : LiveData<List<SensorStatus>>? {
        return mSensorStatusDao.getSensorStatusForName(name)
    }

    fun insertSensorStatus(vararg credentials: SensorStatus?) {
        mSensorStatusDao.insertSensorStatus(*credentials)
    }
}