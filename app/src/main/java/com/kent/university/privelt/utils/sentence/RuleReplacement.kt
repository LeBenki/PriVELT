/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils.sentence

class RuleReplacement(val ending: String, private val replacement: String, private val adding: Boolean) {
    fun apply(word: String): String {
        return if (adding) word + replacement else replacement
    }
}