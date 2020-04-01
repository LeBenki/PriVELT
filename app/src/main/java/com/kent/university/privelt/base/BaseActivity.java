package com.kent.university.privelt.base;

import android.os.Bundle;
import android.view.MenuItem;

import com.kent.university.privelt.PriVELTApplication;
import com.kent.university.privelt.api.PasswordManager;
import com.kent.university.privelt.api.ServiceHelper;
import com.kent.university.privelt.di.DaggerPriVELTComponent;
import com.kent.university.privelt.di.PriVELTComponent;
import com.kent.university.privelt.di.RoomModule;
import com.kent.university.privelt.viewmodel.PriVELTViewModelFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getActivityLayout();

    protected abstract void configureViewModel();

    protected abstract void configureDesign(@Nullable Bundle savedInstanceState);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associates the layout file to this class
        this.setContentView(this.getActivityLayout());

        // Using the ButterKnife library
        ButterKnife.bind(this);

        configureViewModel();

        configureDesign(savedInstanceState);
    }

    protected <T extends ViewModel> T getViewModel(final Class<T> className) {
        // Component
        final PriVELTComponent component = DaggerPriVELTComponent.builder().roomModule(new RoomModule(this)).build();

        // ViewModelFactory
        final PriVELTViewModelFactory factory = component.getViewModelFactory();

       return new ViewModelProvider(this, factory).get(className);
    }

    protected PasswordManager getIdentityManager() {
        return ((PriVELTApplication) getApplication()).getIdentityManager();
    }

    protected ServiceHelper getServiceHelper() {
        return ((PriVELTApplication)getApplicationContext()).getServiceHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((PriVELTApplication) getApplicationContext()).setCurrentActivity(this);
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
