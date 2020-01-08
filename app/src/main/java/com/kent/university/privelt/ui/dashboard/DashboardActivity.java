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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.events.UpdateCredentialsEvent;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.ui.settings.SettingsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

import static com.kent.university.privelt.ui.dashboard.LoginActivity.PARAM_SERVICE;

public class DashboardActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 765;

    private DashboardViewModel dashboardViewModel;

    private ArrayList<Service> subscribedServices;

    @BindView(R.id.recycler_view_services)
    RecyclerView servicesList;

    @BindView(R.id.no_services)
    TextView noService;

    @BindView(R.id.add_service)
    FloatingActionButton addService;

    private DashboardAdapter dashboardAdapter;

    private int currentChoice = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setUpRecyclerView();
        setUpSwipe();
        setUpButton();

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

    private void setUpButton() {
        addService.setOnClickListener(view -> {

            final ArrayAdapter<String> adp = new ArrayAdapter<>(DashboardActivity.this,
                    android.R.layout.simple_spinner_item, ((PriVELT) getApplication()).getServiceHelper().getServiceNames());

            final Spinner sp = new Spinner(DashboardActivity.this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sp.setPadding(50, 50, 50, 50);
            sp.setAdapter(adp);

            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle("Choose the service you want to subscribe to");
            builder.setView(sp);
            builder.setPositiveButton("Choose", (dialogInterface, i) -> {
                currentChoice = sp.getSelectedItemPosition();
                editCredentials(currentChoice);
            });
            builder.create().show();
        });
    }

    private void editCredentials(int service) {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.putExtra(PARAM_SERVICE, service);
        startActivityForResult(intent, REQUEST_LOGIN);
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
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {

            Service service = new Service(((PriVELT)getApplication()).getServiceHelper().getServiceNames().get(currentChoice));

            dashboardViewModel.insertService(service);
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
        //dashboardViewModel.updateService(event.service);
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
