/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.model

import java.util.*

class Application(var name: String) {
    private val permissions: MutableList<String>

    fun getPermissions(): List<String> {
        return permissions
    }

    fun addPermission(permission: String) {
        permissions.add(permission)
    }

    init {
        permissions = ArrayList()
    }
}