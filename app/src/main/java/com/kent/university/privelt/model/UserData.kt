/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "user_data", foreignKeys = [ForeignKey(entity = Service::class, parentColumns = arrayOf("id"), childColumns = arrayOf("service_id"), onDelete = ForeignKey.CASCADE)])
data class UserData(var title: String, var type: String, var value: String, var concatenatedData: String
                    , @field:ColumnInfo(name = "service_id", index = true) var serviceId: Long,
                    var date: Long) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    val unConcatenatedData: Array<String>
        get() = concatenatedData.split(DELIMITER).toTypedArray()

    companion object {
        const val DELIMITER = "@/:-"
    }

}