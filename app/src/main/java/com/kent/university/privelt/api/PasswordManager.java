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
