package com.kent.university.privelt.base;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.api.PasswordManager;
import com.kent.university.privelt.api.ServiceHelper;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected PasswordManager getIdentityManager() {
        return ((PriVELT) getApplication()).getIdentityManager();
    }

    protected ServiceHelper getServiceHelper() {
        return ((PriVELT)getApplicationContext()).getServiceHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((PriVELT) getApplicationContext()).setCurrentActivity(this);
    }
}
