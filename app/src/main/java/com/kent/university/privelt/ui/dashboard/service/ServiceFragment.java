package com.kent.university.privelt.ui.dashboard.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kent.university.privelt.R;
import com.kent.university.privelt.api.ServiceHelper;
import com.kent.university.privelt.base.BaseFragment;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.events.LaunchDataEvent;
import com.kent.university.privelt.events.UpdateCredentialsEvent;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.ui.data.DataActivity;
import com.kent.university.privelt.ui.login.LoginActivity;

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

import static com.kent.university.privelt.api.DataExtraction.processDataExtraction;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_PASSWORD;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_SERVICE;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_USER;

public class ServiceFragment extends BaseFragment {

    private static final int REQUEST_LOGIN = 765;
    private static final int REQUEST_EDIT_LOGIN = 7654;

    private ServiceViewModel serviceViewModel;

    private ArrayList<Service> subscribedServices;

    private ArrayList<UserData> userDatas;

    @BindView(R.id.recycler_view_services)
    RecyclerView servicesList;

    @BindView(R.id.no_services)
    TextView noService;

    @BindView(R.id.add_service)
    FloatingActionButton addService;

    private ServiceAdapter serviceAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_service, container, false);

        ButterKnife.bind(this, view);

        setUpRecyclerView();
        setUpSwipe();
        setupAddButton();

        configureViewModel();
        getServices();
        getUserDatas();
        return view;
    }

    private void getUserDatas() {
        serviceViewModel.getUserDatas().observe(this, this::updateUserDatas);
    }

    private void updateUserDatas(List<UserData> userData) {
        userDatas = new ArrayList<>(userData);
        serviceAdapter.updateServices(subscribedServices);
        serviceAdapter.updateUserDatas(userDatas);
        serviceAdapter.notifyDataSetChanged();
    }

    private void getServices() {
        serviceViewModel.getServices().observe(this, this::updateServices);
    }

    private void updateServices(List<Service> services) {

        subscribedServices = new ArrayList<>(services);

        if (subscribedServices.size() > 0)
            noService.setVisibility(View.GONE);
        else
            noService.setVisibility(View.VISIBLE);

        serviceAdapter.updateServices(subscribedServices);
        serviceAdapter.updateUserDatas(userDatas);
        serviceAdapter.notifyDataSetChanged();
    }

    private void setupAddButton() {
        addService.setOnClickListener(view -> {

            final ArrayAdapter<String> adp = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, getServiceHelper().getRemainingServices(subscribedServices));

            final Spinner sp = new Spinner(getContext());
            sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sp.setPadding(50, 50, 50, 50);
            sp.setAdapter(adp);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.choose_service);
            builder.setView(sp);
            builder.setPositiveButton(R.string.choose, (dialogInterface, i) -> editCredentials(
                    new Service(sp.getSelectedItem().toString(), false, "", "", ""), REQUEST_LOGIN));
            builder.create().show();
        });
    }

    private void editCredentials(Service service, int requestCode) {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra(PARAM_SERVICE, service);
        startActivityForResult(intent, requestCode);
    }

    private void setUpRecyclerView() {

        subscribedServices = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        servicesList.setLayoutManager(layoutManager);
        serviceAdapter = new ServiceAdapter();
        servicesList.setAdapter(serviceAdapter);
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
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.unsubscribe)
                        .setMessage(R.string.unsubscribe_confirmation)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            int position = viewHolder.getAdapterPosition();
                            serviceViewModel.deleteService(subscribedServices.get(position));
                        })
                        .setNegativeButton(R.string.no, (dialogInterface, i) -> serviceAdapter.notifyDataSetChanged())
                        .show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(servicesList);
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        serviceViewModel = ViewModelProviders.of(this, viewModelFactory).get(ServiceViewModel.class);
        serviceViewModel.init();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_LOGIN || requestCode == REQUEST_EDIT_LOGIN) && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Service service = (Service) data.getSerializableExtra(PARAM_SERVICE);

                String user = data.getStringExtra(PARAM_USER);
                String password = data.getStringExtra(PARAM_PASSWORD);

                assert service != null;
                if (service.isPasswordSaved()) {

                    service.setUser(user);
                    service.setPassword(password);
                }

                new AsyncTask<Void, Void , Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if ((requestCode == REQUEST_LOGIN)) {
                            serviceViewModel.insertService(service);
                        } else {
                            serviceViewModel.updateService(service);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (!service.isPasswordSaved())
                            processDataExtraction(new ServiceHelper(getContext()), service, user, password, getContext().getApplicationContext());
                    }
                }.execute();
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

        if (!event.service.isPasswordSaved()) {
            Toast.makeText(getContext(), R.string.authorize_save_password, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(getContext(), DataActivity.class);
        intent.putExtra(PARAM_SERVICE, event.service);
        startActivity(intent);
    }
}
