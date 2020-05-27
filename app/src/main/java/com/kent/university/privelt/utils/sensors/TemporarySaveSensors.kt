/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.sensors

import android.content.Context
import android.os.AsyncTask
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.model.Sensor
import com.kent.university.privelt.model.SensorStatus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

object TemporarySaveSensors {
    private const val SHARED_SENSORS = "shared_sensors"
    private const val SHARED_SENSORS_PARAM = "shared_sensors_param"
    private const val DELIMITER = "@/%"

    fun save(applicationContext: Context, time: Long) {
        val sensors = Sensor.values()

        val json = Json(JsonConfiguration.Stable)

        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_SENSORS, Context.MODE_PRIVATE)
        var sensorsString = sharedPreferences.getString(SHARED_SENSORS_PARAM, "")

        sensors.forEach {
            if (it.isSensor) {
                val sensorStatus = SensorStatus(it.title, time, it.isEnabled(applicationContext))
                sensorsString += json.stringify(SensorStatus.serializer(), sensorStatus) + DELIMITER
            }
        }

        sharedPreferences.edit().putString(SHARED_SENSORS_PARAM, sensorsString).apply()
    }

    fun load(applicationContext: Context) {
        val json = Json(JsonConfiguration.Stable)

        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_SENSORS, Context.MODE_PRIVATE)
        val sensorsString = sharedPreferences.getString(SHARED_SENSORS_PARAM, "")

        val sensors = sensorsString?.split(DELIMITER)

        sensors?.forEach {
            if (it.isNotEmpty()) {
                val obj = json.parse(SensorStatus.serializer(), it)
                //TODO HOTFIX
                PriVELTDatabase.getInstance(applicationContext)
                AsyncTask.execute {
                    kotlin.run {
                        PriVELTDatabase.getInstance(applicationContext)?.sensorStatusDao()?.insertSensorStatus(obj)
                    }
                }
            }
        }

        sharedPreferences.edit().putString(SHARED_SENSORS_PARAM, "").apply()
    }
}