/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "service")
class Service(var name: String, var isPasswordSaved: Boolean, var concatenatedScripts: String, var user: String, var password: String) : Serializable, Cloneable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    val unConcatenatedScripts: Array<String>
        get() = concatenatedScripts.split(DELIMITER).toTypedArray()

    public override fun clone(): Any {
        val service = Service(name, isPasswordSaved, concatenatedScripts, user, password)
        service.id = id
        return service
    }

    companion object {
        const val DELIMITER = "@/:-"
    }

}