/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.master_password

import android.graphics.Color
import com.kent.university.privelt.R

internal enum class PasswordStrength(val resId: Int, val progress: Int, val color: Int) {
    WEAK(R.string.weak, 20, Color.LTGRAY),
    FAIR(R.string.fair, 20, Color.YELLOW),
    GOOD(R.string.good, 60, Color.GREEN),
    STRONG(R.string.strong, 80, Color.BLUE),
    VERY_STRONG(R.string.very_strong, 100, Color.WHITE);
}