/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "permission_status")
class PermissionStatus(var permissionName: String, var date: Long, var wereActivated: Boolean, var applicationPackage: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}