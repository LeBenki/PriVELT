/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.user

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class UserTextWatcher internal constructor(private val editText: EditText, private val myTextWatcher: MyTextWatcher) : TextWatcher {

    interface MyTextWatcher {
        fun afterTextChanged(editText: EditText?, editable: Editable?)
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        myTextWatcher.afterTextChanged(editText, editable)
    }

}