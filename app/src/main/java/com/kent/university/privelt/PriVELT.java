package com.kent.university.privelt;

import android.app.Application;

import com.kent.university.privelt.api.IdentityManager;
import com.kent.university.privelt.utils.ServiceHelper;

import static com.kent.university.privelt.api.IdentityManager.SHARED_PREFERENCES_KEY;

public class PriVELT extends Application {

    private IdentityManager identityManager;
    private ServiceHelper serviceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceManager = new ServiceHelper(this);
        identityManager = new IdentityManager(getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE));
    }

    public ServiceHelper getServiceHelper() {
        return serviceManager;
    }

    public IdentityManager getIdentityManager() {
        return identityManager;
    }
}
