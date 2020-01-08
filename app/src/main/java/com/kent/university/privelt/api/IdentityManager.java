package com.kent.university.privelt.api;

import android.content.SharedPreferences;

public class IdentityManager {

    public final static String SHARED_PREFERENCES_KEY = "SHARED_PREFERENCES_KEY";

    private SharedPreferences sharedPreferences;

    private long mpIndex;

    public IdentityManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        mpIndex = -1;
    }

    public long getMpIndex() {
        return mpIndex;
    }

    public void setMpIndex(long mpIndex) {
        this.mpIndex = mpIndex;
    }
}
