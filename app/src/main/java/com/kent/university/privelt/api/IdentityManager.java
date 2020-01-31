package com.kent.university.privelt.api;

import android.content.SharedPreferences;
import android.text.Editable;

import com.kent.university.privelt.utils.SimpleCrypto;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.security.GeneralSecurityException;

public class IdentityManager {

    public final static String SHARED_PREFERENCES_KEY = "SHARED_PREFERENCES_KEY";
    private final static String KEY_SALT = "KEY_SALT";

    private SharedPreferences sharedPreferences;

    private long mpIndex;
    private Editable password;

    public IdentityManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        mpIndex = -1;
    }

    public Editable getPassword() {
        return Editable.Factory.getInstance().newEditable(password);
    }

    public void setPassword(Editable password) {
        this.password = password;
    }

    public long getMpIndex() {
        return mpIndex;
    }

    public void setMpIndex(long mpIndex) {
        this.mpIndex = mpIndex;
    }

    private String getSalt() {
        if (!sharedPreferences.contains(KEY_SALT)) {
            try {
                sharedPreferences.edit().putString(KEY_SALT, SimpleCrypto.generateSalt()).apply();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
        return sharedPreferences.getString(KEY_SALT, "");
    }

    public AesCbcWithIntegrity.SecretKeys getKey() {
        return getKey(getPassword().toString());
    }

    public AesCbcWithIntegrity.SecretKeys getKey(String password) {
        try {
            return SimpleCrypto.generateKey(password, getSalt());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
