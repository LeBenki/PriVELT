/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils;

import com.nulabinc.zxcvbn.Zxcvbn;

public class PasswordChecker {
    public static boolean checkPassword(Zxcvbn zxcvbn, String password) {
        boolean upper = false;
        boolean lower = false;
        boolean number = false;
        boolean special = false;
        boolean strong = zxcvbn.measure(password).getScore() > 2;
        boolean length = password.length() >= 8;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c))
                number = true;
            else if (Character.isLowerCase(c))
                lower = true;
            else if (Character.isUpperCase(c))
                upper = true;
            else
                special = true;
        }
        return upper && lower && number && special && strong && length;
    }
}
