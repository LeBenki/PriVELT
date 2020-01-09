package com.kent.university.privelt.ui.dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.events.LaunchDataEvent;
import com.kent.university.privelt.events.UpdateCredentialsEvent;
import com.kent.university.privelt.model.Credentials;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.ui.data.DataActivity;
import com.kent.university.privelt.ui.settings.SettingsActivity;
import com.kent.university.privelt.utils.SimpleCrypto;
import com.kent.university.privelt.utils.SimpleHash;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kent.university.privelt.ui.dashboard.LoginActivity.PARAM_PASSWORD;
import static com.kent.university.privelt.ui.dashboard.LoginActivity.PARAM_SERVICE;
import static com.kent.university.privelt.ui.dashboard.LoginActivity.PARAM_SERVICE_ID;
import static com.kent.university.privelt.ui.dashboard.LoginActivity.PARAM_SHOULD_STORE;
import static com.kent.university.privelt.ui.dashboard.LoginActivity.PARAM_USER;

public class DashboardActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 765;
    private static final int REQUEST_EDIT_LOGIN = 7654;

    private DashboardViewModel dashboardViewModel;

    private ArrayList<Service> subscribedServices;

    @BindView(R.id.recycler_view_services)
    RecyclerView servicesList;

    @BindView(R.id.no_services)
    TextView noService;

    @BindView(R.id.add_service)
    FloatingActionButton addService;

    private DashboardAdapter dashboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setUpRecyclerView();
        setUpSwipe();
        setupAddButton();

        configureViewModel();
        getServices();
    }

    private void getServices() {
        dashboardViewModel.getServices().observe(this, this::updateServices);
    }

    private void updateServices(List<Service> services) {

        subscribedServices = new ArrayList<>(services);

        if (subscribedServices.size() > 0)
            noService.setVisibility(View.GONE);
        else
            noService.setVisibility(View.VISIBLE);

        dashboardAdapter.updateServices(subscribedServices);
        dashboardAdapter.notifyDataSetChanged();
    }

    private void setupAddButton() {
        addService.setOnClickListener(view -> {

            final ArrayAdapter<String> adp = new ArrayAdapter<>(DashboardActivity.this,
                    android.R.layout.simple_spinner_item, getServiceHelper().getServiceNames());

            final Spinner sp = new Spinner(DashboardActivity.this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sp.setPadding(50, 50, 50, 50);
            sp.setAdapter(adp);

            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle("Choose the service you want to subscribe to");
            builder.setView(sp);
            builder.setPositiveButton("Choose", (dialogInterface, i) -> {
                editCredentials(sp.getSelectedItem().toString(), REQUEST_LOGIN);
            });
            builder.create().show();
        });
    }

    private void editCredentials(String service, int requestCode) {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.putExtra(PARAM_SERVICE, service);
        startActivityForResult(intent, requestCode);
    }

    private void setUpRecyclerView() {

        subscribedServices = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        servicesList.setLayoutManager(layoutManager);
        dashboardAdapter = new DashboardAdapter(subscribedServices);
        servicesList.setAdapter(dashboardAdapter);
    }

    private void setUpSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                new AlertDialog.Builder(DashboardActivity.this)
                        .setTitle("Unsubscribe")
                        .setMessage("Are you sure you want to unsubscribe to this service?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            int position = viewHolder.getAdapterPosition();
                            dashboardViewModel.deleteService(subscribedServices.get(position));
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dashboardAdapter.notifyDataSetChanged())
                        .show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(servicesList);
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        dashboardViewModel = ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel.class);
        dashboardViewModel.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_LOGIN || requestCode == REQUEST_EDIT_LOGIN) && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                boolean shouldStorePassword = data.getBooleanExtra(PARAM_SHOULD_STORE, false);
                String serviceName = data.getStringExtra(PARAM_SERVICE);

                if (shouldStorePassword) {
                    String user = data.getStringExtra(PARAM_USER);
                    String password = data.getStringExtra(PARAM_PASSWORD);

                    try {
                        dashboardViewModel.updateCredentials(new Credentials(SimpleHash.calculateIndexOfHash(serviceName), user, SimpleCrypto.encrypt(password, getIdentityManager().getKey())));
                    } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }

                if (requestCode == REQUEST_LOGIN) {
                    Service service = new Service(serviceName, shouldStorePassword);
                    dashboardViewModel.insertService(service);
                }
                else {
                    for (Service service : subscribedServices) {
                        if (service.getName().equals(serviceName)) {
                            service.setPasswordSaved(shouldStorePassword);
                            dashboardViewModel.updateService(service);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEditCredentials(UpdateCredentialsEvent event) {
        editCredentials(event.service, REQUEST_EDIT_LOGIN);
    }

    @Subscribe
    public void onLaunchData(LaunchDataEvent event) {

        Service s = null;

        for (Service service : subscribedServices)
            if (service.getId() == event.serviceId)
                s = service;

        if (!s.isPasswordSaved()) {
            Toast.makeText(this, "You did not authorize us to save your password, we cannot process to data extraction.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, DataActivity.class);
        intent.putExtra(PARAM_SERVICE, event.service);
        intent.putExtra(PARAM_SERVICE_ID, event.serviceId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
