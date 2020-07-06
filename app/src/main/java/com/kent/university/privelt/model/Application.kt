/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.model

import kotlin.collections.ArrayList

data class Application(var packageName: String) {
    private val permissions: MutableList<String>
    private val sensors: MutableList<Sensor>

    fun getPermissions(): List<String> {
        return permissions
    }

    fun getSensors(): List<Sensor> {
        return sensors
    }

    fun addPermission(permission: String) {
        permissions.add(permission)
    }

    fun addSensor(sensor: Sensor) {
        sensors.add(sensor)
    }

    init {
        permissions = ArrayList()
        sensors = ArrayList()
    }
}