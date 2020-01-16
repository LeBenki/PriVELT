package com.kent.university.privelt.ui.data;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.webviewautologin.response.ResponseCallback;
import com.kent.university.webviewautologin.response.ResponseEnum;
import com.kent.university.webviewautologin.services.LoginService;
import com.university.kent.dataextractor.DataExtractor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kent.university.privelt.model.UserData.DELIMITER;
import static com.kent.university.privelt.ui.dashboard.DashboardActivity.PARAM_SERVICE_EMAIL;
import static com.kent.university.privelt.ui.dashboard.DashboardActivity.PARAM_SERVICE_PASSWORD;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_SERVICE;

public class DataActivity extends BaseActivity {

    private Service service;

    private String email;

    private String password;

    private LoginService loginService;

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    @BindView(R.id.recycler_view_userdata)
    RecyclerView recyclerView;

    @BindView(R.id.progress_layout)
    LinearLayout progressLayout;

    @BindView(R.id.progress_script)
    ProgressBar progressScript;

    @BindView(R.id.script_name)
    TextView script;

    @BindView(R.id.percent)
    TextView percent;

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

        configureLoginService();

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

        progressLayout.setVisibility(userData.size() == 0 ? View.VISIBLE : View.GONE);
    }

    private void configureLoginService() {
        loginService = getServiceHelper().getServiceWithName(service.getName());

        DataExtractor dataExtractor = new DataExtractor(loginService);
        final ArrayList<UserData> allUserData = new ArrayList<>();

        percent.setText(String.format(getResources().getString(R.string.percent), 0));

        loginService.autoLogin(email, password, new ResponseCallback() {
            @Override
            public void getResponse(ResponseEnum responseEnum, String data) {
                Log.d("LUCAS", responseEnum.toString());
                if (responseEnum != ResponseEnum.SUCCESS) {
                    script.setText(responseEnum.getName());
                } else {
                    script.setText(R.string.logged);
                    dataExtractor.injectAllScriptsByListName(DataActivity.this,(jsonArray, status) -> {
                        int totalData = (int) ((float)((status.getFailedData() + status.getSucceedData())) / (float)(status.getAmountOfData()) * 100);
                        Log.d("LUCAS", String.valueOf(totalData));
                        percent.setText(String.format(getResources().getString(R.string.percent), totalData));
                        progressScript.setProgress(totalData);
                        script.setText(String.format(getResources().getString(R.string.extracting), status.getTaskName()));
                        if (status.isDone()) {
                            Log.d("LUCAS", "LOGIN SERVICE:" + allUserData.size());
                            dataViewModel.replaceUserDatas(allUserData);
                        }
                        Log.d("LUCAS", status.toString());
                        if (status.isFailed() || jsonArray == null)
                            return;
                        allUserData.addAll(parseJSON(jsonArray));
                        Log.d("LUCAS", jsonArray.toString());
                    }, Arrays.asList(service.getUnConcatenatedScripts()));
                }
            }
        });
    }

    private ArrayList<UserData> parseJSON(JSONArray jsonArray) {
        ArrayList<UserData> array = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONArray datas = obj.getJSONArray("data");
                List<String> td = new ArrayList<>();
                for (int j = 0; j < datas.length(); j++) {
                    td.add(datas.getString(j));
                }
                UserData userData = new UserData(
                        obj.getString("title"),
                        obj.getString("type"),
                        obj.getString("value"),
                        TextUtils.join(DELIMITER, td),
                        service.getId());
                Log.d("Response", userData.getType());
                Log.d("Response", userData.getValue());
                Log.d("Response", userData.getConcatenatedData());
                array.add(userData);
            }
        } catch (Exception e) {
            Log.d("Response", Objects.requireNonNull(e.getLocalizedMessage()));
        }
        return array;
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        dataViewModel = ViewModelProviders.of(this, viewModelFactory).get(DataViewModel.class);
        dataViewModel.init();
    }
}
