/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.model.HistoryPermission
import com.kent.university.privelt.model.Sensor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.*

object TemporarySavePermissionsHistory {

    private const val SHARED_PERMISSIONS = "shared_history_permission"
    private const val SHARED_PERMISSIONS_PARAM = "shared_history_permission_param"
    private const val DELIMITER = "@/%"

    fun save(applicationContext: Context, time: Long) {

        val json = Json(JsonConfiguration.Stable)

        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_PERMISSIONS, Context.MODE_PRIVATE)
        var permissionString = sharedPreferences.getString(SHARED_PERMISSIONS_PARAM, "")


        val pm = applicationContext.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val historyPermission = HistoryPermission(time, 0 ,0,
                0,0 ,0, 0 ,0, 0,
                false, bluetoothSensor = false, nfcSensor = false, wifiSensor = false)
        for (applicationInfo in packages) {
            if (SensorHelper.isSystemPackage(applicationInfo)) continue
            try {
                val packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS)
                val requestedPermissions = packageInfo.requestedPermissions
                if (requestedPermissions != null) {
                    for (requestedPermission in requestedPermissions) {
                        //TODO use reflection
                        if (requestedPermission.toLowerCase(Locale.ROOT).contains("Location".toLowerCase(Locale.ROOT))) {
                            historyPermission.locationValue++
                            historyPermission.locationSensor = Sensor.LOCATION.isEnabled(applicationContext)
                        }
                        if (requestedPermission.toLowerCase(Locale.ROOT).contains("Bluetooth".toLowerCase(Locale.ROOT))) {
                            historyPermission.bluetoothValue++
                            historyPermission.bluetoothSensor = Sensor.BLUETOOTH.isEnabled(applicationContext)
                        }
                        if (requestedPermission.toLowerCase(Locale.ROOT).contains("Storage".toLowerCase(Locale.ROOT)))
                        historyPermission.storageValue++
                        if (requestedPermission.toLowerCase(Locale.ROOT).contains("WIFI".toLowerCase(Locale.ROOT))) {
                            historyPermission.wifiValue++
                            historyPermission.wifiSensor = Sensor.WIFI.isEnabled(applicationContext)
                        }
                        if (requestedPermission.toLowerCase(Locale.ROOT).contains("NFC".toLowerCase(Locale.ROOT))) {
                            historyPermission.nfcValue++
                            historyPermission.nfcSensor = Sensor.NFC.isEnabled(applicationContext)
                        }
                        if (requestedPermission.toLowerCase(Locale.ROOT).contains("Contacts".toLowerCase(Locale.ROOT)))
                        historyPermission.contactsValue++
                        if (requestedPermission.toLowerCase(Locale.ROOT).contains("Calendar".toLowerCase(Locale.ROOT)))
                        historyPermission.calendarValue++
                        if (requestedPermission.toLowerCase(Locale.ROOT).contains("SMS".toLowerCase(Locale.ROOT)))
                        historyPermission.smsValue++
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
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