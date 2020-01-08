package com.kent.university.privelt.base;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.utils.ServiceHelper;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public ServiceHelper getServiceHelper() {
        return ((PriVELT)getApplicationContext()).getServiceHelper();
    }
}
