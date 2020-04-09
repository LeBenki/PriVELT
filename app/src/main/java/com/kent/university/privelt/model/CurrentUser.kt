/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_user")
class CurrentUser(var firstName: String, var lastName: String, var birthday: String, var address: String, var phoneNumber: String, var mail: String) {
    @PrimaryKey
    var id: Long = 0

}