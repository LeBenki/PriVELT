package com.kent.university.privelt.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.model.Service;
import com.kent.university.webviewautologin.response.ResponseCallback;
import com.kent.university.webviewautologin.response.ResponseEnum;
import com.kent.university.webviewautologin.services.LoginService;
import com.university.kent.dataextractor.DataExtractor;

import java.util.Arrays;
import java.util.Collections;

import static com.kent.university.privelt.utils.EyePassword.configureEye;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String PARAM_SHOULD_STORE = "PARAM_SHOULD_STORE";
    public static final String PARAM_USER = "PARAM_USER";
    public static final String PARAM_PASSWORD = "PARAM_PASSWORD";

    public static final String PARAM_SERVICE = "PARAM_SERVICE";
    public static final String PARAM_SERVICE_ID = "PARAM_SERVICE_ID";

    private Service service;

    @BindView(R.id.best_poc)
    Button button;

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

//    @BindView(R.id.debug)
//    Button debug;

    @BindView(R.id.scripts)
    RecyclerView recyclerView;

    @BindView(R.id.eye_password)
    ImageView eye;

    @BindView(R.id.remember_password)
    CheckBox rememberPassword;

    LoginService loginService = null;
    AlertDialog alertDialog = null;

    private ScriptsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        button.setOnClickListener(this);

        if (getIntent() != null) {
            service = (Service) getIntent().getSerializableExtra(PARAM_SERVICE);
        }
        else if (savedInstanceState != null) {
            service = (Service) savedInstanceState.getSerializable(PARAM_SERVICE);
        }

        setTitle(service.getName());

        /*debug.setOnClickListener(view -> showAlertDebug());*/

        configureEye(new Pair<>(eye, password));

        loginService = getServiceHelper().getServiceWithName(service.getName());

        configureRecyclerView();
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataExtractor dataExtractor = new DataExtractor(loginService);
        dataExtractor.getServiceName();
        Log.d("LULU", service.getConcatenatedScripts());
        adapter = new ScriptsAdapter(dataExtractor.getStringScripts(), Arrays.asList(service.getUnConcatenatedScripts()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PARAM_SERVICE, service);
    }

    public void processLogin() {

        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);

        loginService.autoLogin(email.getText().toString(), password.getText().toString(), new ResponseCallback() {
            @Override
            public void getResponse(ResponseEnum responseEnum, String data) {
                if (responseEnum != ResponseEnum.SUCCESS) {
                    button.setEnabled(true);

                    showAlertDebug();

                    Toast.makeText(LoginActivity.this, responseEnum.getName(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter an email with a password", Toast.LENGTH_LONG).show();
            return;
        }

        processLogin();
    }

    private void showAlertDebug() {

        if (loginService == null) {
            Toast.makeText(LoginActivity.this, "Login service is not instanced yet", Toast.LENGTH_LONG).show();
            return;
        }

        if (loginService.getWebview().getParent() != null) {
            ((ViewGroup)loginService.getWebview().getParent()).removeView(loginService.getWebview());
        }

        if (alertDialog == null || ! alertDialog.isShowing()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
            dialogBuilder.setView(loginService.getWebview());
            alertDialog = dialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.check_menu, menu);

        return true;
    }

    public boolean isValidInput() {
        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter an email and a password", Toast.LENGTH_LONG).show();
            return false;
        }
        if (adapter.getConcatenatedScriptsChecked().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please choose at least one extraction method", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check:
                if (isValidInput()) {
                    Intent intent = new Intent();
                    intent.putExtra(PARAM_USER, email.getText().toString());
                    intent.putExtra(PARAM_PASSWORD, password.getText().toString());

                    service.setPasswordSaved(rememberPassword.isChecked());
                    service.setConcatenatedScripts(adapter.getConcatenatedScriptsChecked());
                    intent.putExtra(PARAM_SERVICE, service);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}