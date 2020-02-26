package com.kent.university.privelt.ui.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.service.ProcessMainClass;
import com.kent.university.privelt.service.restarter.RestartServiceBroadcastReceiver;
import com.kent.university.privelt.ui.dashboard.card.CardFragment;
import com.kent.university.privelt.ui.dashboard.user.UserFragment;
import com.kent.university.privelt.ui.settings.SettingsActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends BaseActivity {

    private ActionBar toolbar;

    @BindView(R.id.navigation_view)
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);

        toolbar = getSupportActionBar();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle(R.string.services);
        loadFragment(new CardFragment());
        launchService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(R.string.data_extraction).setMessage(R.string.overlay_permission).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    this.startActivity(myIntent);
                }).setNegativeButton(R.string.no, null);
                alertDialog.show();
                return;
            }
        }
    }

    public void launchService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_service:
                    toolbar.setTitle(R.string.services);
                    fragment = new CardFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_user:
                    toolbar.setTitle(R.string.user);
                    fragment = new UserFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem settings = menu.findItem(R.id.settings);
        MenuItem edit = menu.findItem(R.id.edit);
        MenuItem check = menu.findItem(R.id.check);
        MenuItem filter = menu.findItem(R.id.filter);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if(fragment instanceof UserFragment)
        {
            settings.setVisible(true);
            edit.setVisible(true);
            check.setVisible(false);
            filter.setVisible(false);
        }
        else
        {
            settings.setVisible(false);
            edit.setVisible(false);
            check.setVisible(false);
            filter.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
