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
import com.kent.university.privelt.model.UserData;
import com.kent.university.webviewautologin.response.ResponseCallback;
import com.kent.university.webviewautologin.response.ResponseEnum;
import com.kent.university.webviewautologin.services.LoginService;
import com.university.kent.dataextractor.DataExtractor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
import static com.kent.university.privelt.ui.dashboard.LoginActivity.PARAM_SERVICE;
import static com.kent.university.privelt.ui.dashboard.LoginActivity.PARAM_SERVICE_ID;

public class DataActivity extends BaseActivity {

    private String service;

    private String email;

    private String password;

    private long serviceId;

    LoginService loginService;

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
            service = savedInstanceState.getString(PARAM_SERVICE, "");
            email = savedInstanceState.getString(PARAM_SERVICE_EMAIL, "");
            password = savedInstanceState.getString(PARAM_SERVICE_PASSWORD, "");
            serviceId = savedInstanceState.getLong(PARAM_SERVICE_ID, -1);
        } else if (getIntent() != null) {
            service = getIntent().getStringExtra(PARAM_SERVICE);
            email = getIntent().getStringExtra(PARAM_SERVICE_EMAIL);
            password = getIntent().getStringExtra(PARAM_SERVICE_PASSWORD);
            serviceId = getIntent().getLongExtra(PARAM_SERVICE_ID, -1);
        }
        setTitle(service);

        configureRecyclerView();

        configureLoginService();

        configureViewModel();

        getUserDatas();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAM_SERVICE, service);
        outState.putString(PARAM_SERVICE_EMAIL, email);
        outState.putString(PARAM_SERVICE_PASSWORD, password);
        outState.putLong(PARAM_SERVICE_ID, serviceId);
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
        loginService = getServiceHelper().getServiceWithName(service);

        DataExtractor dataExtractor = new DataExtractor(loginService);
        final ArrayList<UserData> allUserData = new ArrayList<>();

        loginService.autoLogin(email, password, new ResponseCallback() {
            @Override
            public void getResponse(ResponseEnum responseEnum, String data) {
                Log.d("LUCAS", responseEnum.toString());
                if (responseEnum != ResponseEnum.SUCCESS) {
                    script.setText(responseEnum.getName());
                } else {
                    script.setText("Logged");
                    dataExtractor.injectAll(DataActivity.this, (jsonArray, status) -> {
                        int totalData = (int) ((float)((status.getFailedData() + status.getSucceedData())) / (float)(status.getAmountOfData()) * 100);
                        Log.d("LUCAS", String.valueOf(totalData));
                        percent.setText(String.valueOf(totalData).concat("%"));
                        progressScript.setProgress(totalData);
                        script.setText(new String("Extracting: ").concat(status.getTaskName()));
                        if (status.isDone()) {
                            Log.d("LUCAS", "LOGIN SERVICE:" + String.valueOf(allUserData.size()));
                            dataViewModel.replaceUserDatas(allUserData);
                        }
                        Log.d("LUCAS", status.toString());
                        if (status.isFailed() || jsonArray == null)
                            return;
                        allUserData.addAll(parseJSON(jsonArray));
                        Log.d("LUCAS", jsonArray.toString());
                    });
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
                        serviceId);
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
