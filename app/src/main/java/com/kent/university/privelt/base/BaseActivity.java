package com.kent.university.privelt.base;

import android.view.MenuItem;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.api.PasswordManager;
import com.kent.university.privelt.api.ServiceHelper;

import androidx.annotation.NonNull;
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
