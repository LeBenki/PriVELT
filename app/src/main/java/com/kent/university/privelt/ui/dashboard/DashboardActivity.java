package com.kent.university.privelt.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
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
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.ui.data.DataActivity;
import com.kent.university.privelt.ui.login.LoginActivity;
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

import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_PASSWORD;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_SERVICE;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_USER;

public class DashboardActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 765;
    private static final int REQUEST_EDIT_LOGIN = 7654;
    public static final String PARAM_SERVICE_EMAIL = "PARAM_SERVICE_EMAIL";
    public static final String PARAM_SERVICE_PASSWORD = "PARAM_SERVICE_PASSWORD";

    private DashboardViewModel dashboardViewModel;

    private ArrayList<Service> subscribedServices;

    private ArrayList<UserData> userDatas;

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
        getUserDatas();
    }

    private void getUserDatas() {
        dashboardViewModel.getUserDatas().observe(this, this::updateUserDatas);
    }

    private void updateUserDatas(List<UserData> userData) {
        userDatas = new ArrayList<>(userData);
        dashboardAdapter.updateServices(subscribedServices);
        dashboardAdapter.updateUserDatas(userDatas);
        dashboardAdapter.notifyDataSetChanged();
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
        dashboardAdapter.updateUserDatas(userDatas);
        dashboardAdapter.notifyDataSetChanged();
    }

    private void setupAddButton() {
        addService.setOnClickListener(view -> {

            final ArrayAdapter<String> adp = new ArrayAdapter<>(DashboardActivity.this,
                    android.R.layout.simple_spinner_item, getServiceHelper().getRemainingServices(subscribedServices));

            final Spinner sp = new Spinner(DashboardActivity.this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sp.setPadding(50, 50, 50, 50);
            sp.setAdapter(adp);

            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle("Choose the service you want to subscribe to");
            builder.setView(sp);
            builder.setPositiveButton("Choose", (dialogInterface, i) -> editCredentials(new Service(sp.getSelectedItem().toString(), false , ""), REQUEST_LOGIN));
            builder.create().show();
        });
    }

    private void editCredentials(Service service, int requestCode) {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.putExtra(PARAM_SERVICE, service);
        startActivityForResult(intent, requestCode);
    }

    private void setUpRecyclerView() {

        subscribedServices = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        servicesList.setLayoutManager(layoutManager);
        dashboardAdapter = new DashboardAdapter();
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
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
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
                Service service = (Service) data.getSerializableExtra(PARAM_SERVICE);

                assert service != null;
                if (service.isPasswordSaved()) {
                    String user = data.getStringExtra(PARAM_USER);
                    String password = data.getStringExtra(PARAM_PASSWORD);

                    try {
                        assert user != null;
                        dashboardViewModel.updateCredentials(new Credentials(SimpleHash.calculateIndexOfHash(service.getName()), user, SimpleCrypto.encrypt(password, getIdentityManager().getKey())));
                    } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
                if ((requestCode == REQUEST_LOGIN)) {
                    dashboardViewModel.insertService(service);
                } else {
                    dashboardViewModel.updateService(service);
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

    @SuppressLint("StaticFieldLeak")
    @Subscribe
    public void onLaunchData(LaunchDataEvent event) {

        if (!event.service.isPasswordSaved()) {
            Toast.makeText(this, "You did not authorize us to save your password, we cannot process to data extraction.", Toast.LENGTH_LONG).show();
            return;
        }

        new AsyncTask<Void, Void, Pair<String, String>>() {

            @Override
            protected Pair<String, String> doInBackground(Void... voids) {
                Credentials credentials = dashboardViewModel.getCredentialsWithId(event.service.getCredentialsId());
                String email = credentials.getEmail();
                String password = "";
                try {
                    password = SimpleCrypto.decrypt(credentials.getPassword(), getIdentityManager().getKey());
                } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                    e.printStackTrace();
                }
                return new Pair<>(email, password);
            }

            @Override
            protected void onPostExecute(Pair<String, String> pair) {
                super.onPostExecute(pair);
                Intent intent = new Intent(DashboardActivity.this, DataActivity.class);
                intent.putExtra(PARAM_SERVICE, event.service);
                intent.putExtra(PARAM_SERVICE_EMAIL, pair.first);
                intent.putExtra(PARAM_SERVICE_PASSWORD, pair.second);
                startActivity(intent);
            }
        }.execute();
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
