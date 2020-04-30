/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kent.university.privelt.model.SensorStatus

@Dao
interface SensorStatusDao {
    @get:Query("SELECT * FROM sensor_status")
    val sensorStatus: LiveData<List<SensorStatus>>?

    @Query("SELECT * FROM sensor_status WHERE sensorName = :name")
    fun getSensorStatusForName(name: String): LiveData<List<SensorStatus>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSensorStatus(vararg sensorStatus: SensorStatus?)
}