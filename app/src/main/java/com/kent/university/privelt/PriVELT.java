package com.kent.university.privelt;

import android.app.Application;

import com.kent.university.privelt.api.IdentityManager;

import static com.kent.university.privelt.api.IdentityManager.SHARED_PREFERENCES_KEY;

public class PriVELT extends Application {

    private IdentityManager identityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        identityManager = new IdentityManager(getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE));
    }

    public IdentityManager getIdentityManager() {
        return identityManager;
    }
}
