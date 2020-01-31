package com.kent.university.privelt.api;

import android.text.Editable;

import static com.kent.university.privelt.database.PriVELTDatabase.changeMasterPassword;

public class PasswordManager {

    private Editable password;

    public Editable getPassword() {
        if (password != null)
            return Editable.Factory.getInstance().newEditable(password);
        else
            return null;
    }

    public void setPassword(Editable password) {
        this.password = password;
    }

    public void changePassword(Editable password) {
        this.password = password;
        changeMasterPassword();
    }
}
