/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.sensors

import android.content.Context
import android.os.AsyncTask
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.model.HistoryPermission
import com.kent.university.privelt.utils.sensors.SensorHelper.getSensorsInformation
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.*
import kotlin.reflect.KMutableProperty

object TemporarySaveHistoryPermission {

    private const val SHARED_PERMISSIONS = "shared_history_permission"
    private const val SHARED_PERMISSIONS_PARAM = "shared_history_permission_param"
    private const val DELIMITER = "@/%"

    fun save(applicationContext: Context, time: Long) {

        val json = Json(JsonConfiguration.Stable)

        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_PERMISSIONS, Context.MODE_PRIVATE)
        var permissionString = sharedPreferences.getString(SHARED_PERMISSIONS_PARAM, "")

        val historyPermission = HistoryPermission(time, 0, 0,
                0, 0, 0, 0, 0, 0,
                false, bluetoothSensor = false, nfcSensor = false, wifiSensor = false,
                microValue = 0, cameraValue = 0, accountsValue = 0, activityrecognitionValue = 0,
                bodysensorsValue = 0, networkstateValue = 0, phonestateValue = 0, playingcontentValue = 0)

        val sensors = getSensorsInformation(context = applicationContext)
        for (sensor in sensors) {
            val kClass = Class.forName(historyPermission.javaClass.name).kotlin

            //Remove space and uppercase letters
            val value = kClass.members.filterIsInstance<KMutableProperty<*>>().firstOrNull { it.name == sensor.title.toLowerCase(Locale.ROOT).replace(" ", "") + "Value" }
            val sensorValue = kClass.members.filterIsInstance<KMutableProperty<*>>().firstOrNull { it.name == sensor.title.toLowerCase(Locale.ROOT).replace(" ", "") + "Sensor" }

            value?.setter?.call(historyPermission, sensor.getApplications().size)

            if (sensor.isSensor)
                sensorValue?.setter?.call(historyPermission, sensor.isEnabled(applicationContext))
        }
        permissionString += json.stringify(HistoryPermission.serializer(), historyPermission) + DELIMITER
        sharedPreferences.edit().putString(SHARED_PERMISSIONS_PARAM, permissionString).apply()
    }

    fun load(applicationContext: Context) {
        val json = Json(JsonConfiguration.Stable)

        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_PERMISSIONS, Context.MODE_PRIVATE)
        val permissionString = sharedPreferences.getString(SHARED_PERMISSIONS_PARAM, "")

        val permission = permissionString?.split(DELIMITER)

        permission?.forEach {
            if (it.isNotEmpty()) {
                val obj = json.parse(HistoryPermission.serializer(), it)
                //TODO HOTFIX
                PriVELTDatabase.getInstance(applicationContext)
                AsyncTask.execute {
                    kotlin.run {
                        PriVELTDatabase.getInstance(applicationContext)?.historyPermissionDao()?.insertHistoryPermissionStatus(obj)
                    }
                }
            }
        }

        sharedPreferences.edit().putString(SHARED_PERMISSIONS_PARAM, "").apply()
    }
}