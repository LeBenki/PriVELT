/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils;

import android.annotation.SuppressLint;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;

public class EyePassword {

    @SuppressLint("ClickableViewAccessibility")
    public static void configureEye(ImageView image, EditText editText) {
        image.setOnTouchListener((v, event) -> showEye(event, editText));
    }

    private static boolean showEye(MotionEvent event, EditText editText) {
        if (editText.getText().toString().isEmpty())
            return false;

        switch (event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case MotionEvent.ACTION_UP:
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }
        return true;
    }
}
