package com.kent.university.privelt.ui.data;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
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

    private Service service;

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
            service = (Service) savedInstanceState.getSerializable(PARAM_SERVICE);
        } else if (getIntent() != null) {
            service = (Service) getIntent().getSerializableExtra(PARAM_SERVICE);
        }

        setTitle(service.getName());

        configureRecyclerView();

        configureViewModel();

        getUserDatas();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PARAM_SERVICE, service);
    }

    private void configureRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataAdapter = new DataAdapter(new ArrayList<>());
        recyclerView.setAdapter(dataAdapter);
    }

    private void getUserDatas() {
        dataViewModel.getUserDatasForService(service.getId()).observe(this, this::updateUserData);
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
        dataViewModel.init(service.getId());
    }
}
