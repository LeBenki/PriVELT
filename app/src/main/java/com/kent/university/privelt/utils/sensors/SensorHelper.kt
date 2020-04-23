/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils.sensors

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.kent.university.privelt.model.Application
import com.kent.university.privelt.model.Sensor
import java.util.*

object SensorHelper {
    private fun isSystemPackage(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private fun checkIfApplicationHasPermission(application: Application, sensor: String): Boolean {
        for (permission in application.getPermissions()) {
            if (permission.toLowerCase(Locale.ROOT).contains(sensor.toLowerCase(Locale.ROOT))) return true
        }
        return false
    }

    fun getSensorsInformation(context: Context): List<Sensor> {
        val applications = getApplicationsInformation(context)
        val sensors = listOf(*Sensor.values())
        for (sensor in sensors) {
            sensor.getApplications().clear()
            for (application in applications) {
                if (checkIfApplicationHasPermission(application, sensor.title)) {
                    sensor.addApplication(application)
                }
            }
        }
        return sensors
    }

    private fun getApplicationsInformation(context: Context): List<Application> {
        val applications: MutableList<Application> = ArrayList()
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in packages) {
            if (isSystemPackage(applicationInfo)) continue
            val application = Application(applicationInfo.packageName)
            try {
                val packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS)
                val requestedPermissions = packageInfo.requestedPermissions
                if (requestedPermissions != null) {
                    for (requestedPermission in requestedPermissions) {
                        application.addPermission(requestedPermission)
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            applications.add(application)
        }
        return applications
    }
}