/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils

import com.nulabinc.zxcvbn.Zxcvbn

object PasswordChecker {
    fun checkPassword(zxcvbn: Zxcvbn, password: String): Boolean {
        var upper = false
        var lower = false
        var number = false
        var special = false
        val strong = zxcvbn.measure(password).score > 2
        val length = password.length >= 8
        for (c in password.toCharArray()) {
            if (Character.isDigit(c)) number = true else if (Character.isLowerCase(c)) lower = true else if (Character.isUpperCase(c)) upper = true else special = true
        }
        return upper && lower && number && special && strong && length
    }
}