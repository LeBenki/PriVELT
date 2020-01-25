package com.kent.university.privelt.ui.data;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import static com.kent.university.privelt.ui.dashboard.service.ServiceFragment.PARAM_SERVICE_EMAIL;
import static com.kent.university.privelt.ui.dashboard.service.ServiceFragment.PARAM_SERVICE_PASSWORD;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_SERVICE;

public class DataActivity extends BaseActivity {

    private Service service;

    private String email;

    private String password;

    @BindView(R.id.recycler_view_userdata)
    RecyclerView recyclerView;

    /*
    @BindView(R.id.progress_layout)
    LinearLayout progressLayout;

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    @BindView(R.id.progress_script)
    ProgressBar progressScript;

    @BindView(R.id.script_name)
    TextView script;

    @BindView(R.id.percent)
    TextView percent;*/

    private DataViewModel dataViewModel;

    private DataAdapter dataAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_data);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            service = (Service) savedInstanceState.getSerializable(PARAM_SERVICE);
            email = savedInstanceState.getString(PARAM_SERVICE_EMAIL, "");
            password = savedInstanceState.getString(PARAM_SERVICE_PASSWORD, "");
        } else if (getIntent() != null) {
            service = (Service) getIntent().getSerializableExtra(PARAM_SERVICE);
            email = getIntent().getStringExtra(PARAM_SERVICE_EMAIL);
            password = getIntent().getStringExtra(PARAM_SERVICE_PASSWORD);
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
        outState.putString(PARAM_SERVICE_EMAIL, email);
        outState.putString(PARAM_SERVICE_PASSWORD, password);
    }

    private void configureRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataAdapter = new DataAdapter(new ArrayList<>());
        recyclerView.setAdapter(dataAdapter);
    }

    private void getUserDatas() {
        dataViewModel.getUserDatas().observe(this, this::updateUserData);
    }

    private void updateUserData(List<UserData> userData) {

        Log.d("LUCAS", String.valueOf(userData.size()));
        dataAdapter.setUserData(userData);
        dataAdapter.notifyDataSetChanged();

//        progressLayout.setVisibility(userData.size() == 0 ? View.VISIBLE : View.GONE);
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        dataViewModel = ViewModelProviders.of(this, viewModelFactory).get(DataViewModel.class);
        dataViewModel.init();
    }
}
