/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.api

import android.text.Editable
import com.kent.university.privelt.database.PriVELTDatabase.changeMasterPassword

class PasswordManager {
    var password: Editable? = null

    fun changePassword(password: Editable?) {
        this.password = password
        changeMasterPassword()
    }
}