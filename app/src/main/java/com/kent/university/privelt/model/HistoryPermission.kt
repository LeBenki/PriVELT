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
@Entity(tableName = "history_permission")
data class HistoryPermission (var date: Long, var locationValue: Int, var bluetoothValue: Int,
                              var storageValue: Int, var wifiValue: Int, var nfcValue:Int,
                                var contactsValue: Int, var calendarValue: Int, var smsValue: Int,
                                var locationSensor: Boolean, var bluetoothSensor: Boolean,
                                var nfcSensor: Boolean, var wifiSensor: Boolean) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}