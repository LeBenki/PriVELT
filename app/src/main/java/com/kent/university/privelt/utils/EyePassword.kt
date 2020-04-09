/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils

import android.annotation.SuppressLint
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView

object EyePassword {
    @SuppressLint("ClickableViewAccessibility")
    fun configureEye(image: ImageView, editText: EditText) {
        image.setOnTouchListener { v: View?, event: MotionEvent -> showEye(event, editText) }
    }

    private fun showEye(event: MotionEvent, editText: EditText): Boolean {
        if (editText.text.toString().isEmpty()) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> editText.inputType = InputType.TYPE_CLASS_TEXT
            MotionEvent.ACTION_UP -> editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        return true
    }
}