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
