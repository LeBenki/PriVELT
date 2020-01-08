package com.kent.university.privelt.api;

import android.content.SharedPreferences;

import com.kent.university.privelt.utils.SimpleCrypto;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.security.GeneralSecurityException;

public class IdentityManager {

    public final static String SHARED_PREFERENCES_KEY = "SHARED_PREFERENCES_KEY";
    public final static String KEY_SALT = "KEY_SALT";

    private SharedPreferences sharedPreferences;

    private long mpIndex;
    private String password;

    public IdentityManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        mpIndex = -1;
        password = "";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getMpIndex() {
        return mpIndex;
    }

    public void setMpIndex(long mpIndex) {
        this.mpIndex = mpIndex;
    }

    public String getSalt() {
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
        try {
            String test = getPassword();
            String test2 = getSalt();
            return SimpleCrypto.generateKey(getPassword(), getSalt());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
