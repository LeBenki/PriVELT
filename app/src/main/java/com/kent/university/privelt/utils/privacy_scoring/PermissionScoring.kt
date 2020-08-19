/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.privacy_scoring

import android.content.Context
import com.kent.university.privelt.model.Sensor
import com.kent.university.privelt.utils.sensors.SensorHelper.getApplicationWithSensorsInformation
import kotlin.math.min

object PermissionScoring {

    private fun median(list: List<Double>) = list.sorted().let {
        (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
    }

    fun computeGlobalScore(context: Context): Int {
        val total = ArrayList<Double>()
        val apps = getApplicationWithSensorsInformation(context)
        for (app in apps) {
            var appScore = 0.0
            for (sensor in app.getSensors())
            {
                appScore += if (!sensor.isSensor || (sensor.isSensor && sensor.isEnabled(context)))
                    if (sensor.isDangerous) 25 else 10
                else
                //Unactivated sensor
                    if (sensor.isDangerous) 10 else 5
            }
            appScore = min(appScore, 100.0)
            total.add(appScore)
        }
        return median(total).toInt()
    }

    fun computeScoreForPermission(sensor: Sensor, numberOfApps: Int): Float {
        return sensor.getApplications().size.toFloat() / numberOfApps.toFloat() * 100f
    }
}