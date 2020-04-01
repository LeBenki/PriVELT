package com.kent.university.privelt;

import android.app.Application;

import com.kent.university.privelt.api.PasswordManager;
import com.kent.university.privelt.api.ServiceHelper;

import androidx.appcompat.app.AppCompatActivity;

public class PriVELTApplication extends Application{

    private PasswordManager identityManager;
    private ServiceHelper serviceManager;
    private AppCompatActivity activity;
    private static volatile PriVELTApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        serviceManager = new ServiceHelper(this);
        identityManager = new PasswordManager();
    }

    public static PriVELTApplication getInstance() {
        return INSTANCE;
    }

    public ServiceHelper getServiceHelper() {
        return serviceManager;
    }

    public PasswordManager getIdentityManager() {
        return identityManager;
    }

    public void setCurrentActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public AppCompatActivity getCurrentActivity() {
        return activity;
    }
}
