/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.user;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class UserTextWatcher implements TextWatcher {

    private EditText editText;

    private MyTextWatcher myTextWatcher;

    UserTextWatcher(EditText editText, MyTextWatcher myTextWatcher) {
        this.editText = editText;
        this.myTextWatcher = myTextWatcher;
    }

    public interface MyTextWatcher {
        void afterTextChanged(EditText editText, Editable editable);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        myTextWatcher.afterTextChanged(editText, editable);
    }
}
