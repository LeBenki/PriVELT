package com.kent.university.privelt.ui.data;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.injections.Injection;
import com.kent.university.privelt.injections.ViewModelFactory;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_SERVICE;

public class DataActivity extends BaseActivity {

    public static final String PARAM_TYPE = "PARAM_TYPE";
    private String service;

    private String type;

    @BindView(R.id.recycler_view_userdata)
    RecyclerView recyclerView;

    @BindView(R.id.progress_layout)
    LinearLayout progressLayout;

    private DataViewModel dataViewModel;

    private DataAdapter dataAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_data);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            service = savedInstanceState.getString(PARAM_SERVICE);
            type = savedInstanceState.getString(PARAM_TYPE);
        } else if (getIntent() != null) {
            service = getIntent().getStringExtra(PARAM_SERVICE);
            type = getIntent().getStringExtra(PARAM_TYPE);
        }

//        setTitle(service);

        configureRecyclerView();

        configureViewModel();

        getServices();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PARAM_SERVICE, service);
        outState.putSerializable(PARAM_TYPE, type);
    }

    private void configureRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataAdapter = new DataAdapter(new ArrayList<>());
        recyclerView.setAdapter(dataAdapter);
    }

    private void getServices() {
        dataViewModel.getServices().observe(this, this::updateServices);
    }

    private void getUserDatas(long id, String type) {
        dataViewModel.getUserData(id, type).observe(this, this::updateUserData);
    }

    private void updateServices(List<Service> services) {

        long id = -1;

        for (Service service : services)
            if (service.getName().equals(this.service))
                id = service.getId();

        getUserDatas(id, type);
    }

    private void updateUserData(List<UserData> userData) {
        if (userData.isEmpty()) {
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            dataAdapter.setUserData(userData);
            dataAdapter.notifyDataSetChanged();
            progressLayout.setVisibility(View.GONE);
        }
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        dataViewModel = ViewModelProviders.of(this, viewModelFactory).get(DataViewModel.class);
        dataViewModel.init();
    }
}
