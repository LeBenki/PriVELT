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
import com.kent.university.privelt.model.PermissionStatus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

object TemporarySavePermissions {

    private const val SHARED_PERMISSIONS = "shared_permission"
    private const val SHARED_PERMISSIONS_PARAM = "shared_permission_param"
    private const val DELIMITER = "@/%"

    fun save(applicationContext: Context) {

        val currentTimestamp = System.currentTimeMillis()

        val json = Json(JsonConfiguration.Stable)

        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_PERMISSIONS, Context.MODE_PRIVATE)
        var permissionString = sharedPreferences.getString(SHARED_PERMISSIONS_PARAM, "")


        val pm = applicationContext.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in packages) {
            if (SensorHelper.isSystemPackage(applicationInfo)) continue
            try {
                val packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS)
                val requestedPermissions = packageInfo.requestedPermissions
                if (requestedPermissions != null) {
                    for ((i, requestedPermission) in requestedPermissions.withIndex()) {
                        val permission = PermissionStatus(requestedPermission, currentTimestamp, SensorHelper.getIfPermissionWereGranted(packageInfo, i), applicationInfo.packageName)
                        permissionString += json.stringify(PermissionStatus.serializer(), permission) + DELIMITER
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        sharedPreferences.edit().putString(SHARED_PERMISSIONS_PARAM, permissionString).apply()
    }

    fun load(applicationContext: Context) {
        val json = Json(JsonConfiguration.Stable)

        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_PERMISSIONS, Context.MODE_PRIVATE)
        val permissionString = sharedPreferences.getString(SHARED_PERMISSIONS_PARAM, "")

        val permission = permissionString?.split(DELIMITER)

        permission?.forEach {
            if (it.isNotEmpty()) {
                val obj = json.parse(PermissionStatus.serializer(), it)
                //TODO HOTFIX
                PriVELTDatabase.getInstance(applicationContext)
                AsyncTask.execute {
                    kotlin.run {
                        PriVELTDatabase.getInstance(applicationContext)?.permissionStatusDao()?.insertPermissionStatus(obj)
                    }
                }
            }
        }

        sharedPreferences.edit().putString(SHARED_PERMISSIONS_PARAM, "").apply()
    }
}