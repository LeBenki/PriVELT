/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.api;

import android.text.Editable;

import static com.kent.university.privelt.database.PriVELTDatabase.changeMasterPassword;

public class PasswordManager {

    private Editable password;

    public Editable getPassword() {
        return password;
    }

    public void setPassword(Editable password) {
        this.password = password;
    }

    public void changePassword(Editable password) {
        this.password = password;
        changeMasterPassword();
    }
}
