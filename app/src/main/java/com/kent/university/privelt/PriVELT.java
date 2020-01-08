package com.kent.university.privelt;

import android.app.Application;

import com.kent.university.privelt.utils.ServiceHelper;

public class PriVELT extends Application {

    private ServiceHelper serviceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceManager = new ServiceHelper(this);
    }

    public ServiceHelper getServiceHelper() {
        return serviceManager;
    }
}
