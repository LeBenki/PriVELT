package com.kent.university.privelt.base;

import com.kent.university.privelt.api.IdentityManager;
import com.kent.university.privelt.PriVELT;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected IdentityManager getIdentityManager() {
        return ((PriVELT) getApplication()).getIdentityManager();
    }
}
