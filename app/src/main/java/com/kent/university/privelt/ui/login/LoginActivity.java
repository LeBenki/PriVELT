/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.kent.university.privelt.BuildConfig;
import com.kent.university.privelt.R;
import com.kent.university.privelt.api.ServiceHelper;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.model.Service;
import com.kent.university.webviewautologin.response.ResponseCallback;
import com.kent.university.webviewautologin.response.ResponseEnum;
import com.kent.university.webviewautologin.services.LoginService;
import com.university.kent.dataextractor.DataExtractor;

import java.util.Arrays;

import static com.kent.university.privelt.utils.EyePassword.configureEye;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public static final String PARAM_USER = "PARAM_USER";
    public static final String PARAM_PASSWORD = "PARAM_PASSWORD";

    public static final String PARAM_SERVICE = "PARAM_SERVICE";

    private Service service;

    @BindView(R.id.best_poc)
    Button button;

    @BindView(R.id.mail)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

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
    protected int getActivityLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void configureViewModel() {

    }

    @Override
    protected void configureDesign(Bundle savedInstanceState) {

        button.setOnClickListener(this);

        if (getIntent() != null) {
            service = (Service) getIntent().getSerializableExtra(PARAM_SERVICE);
        }
        else if (savedInstanceState != null) {
            service = (Service) savedInstanceState.getSerializable(PARAM_SERVICE);
        }

        assert service != null;
        setTitle(service.getName());

        configureEye(eye, password);

        ServiceHelper serviceHelper = new ServiceHelper(this);
        loginService = serviceHelper.getServiceWithName(service.getName());

        rememberPassword.setChecked(service.isPasswordSaved());
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataExtractor dataExtractor = new DataExtractor(loginService);
        dataExtractor.getServiceName();
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

        if (BuildConfig.DEBUG)
            showAlertDebug();

        loginService.autoLogin(email.getText().toString(), password.getText().toString(), new ResponseCallback() {
            @Override
            public void getResponse(ResponseEnum responseEnum, String data) {
                if (responseEnum != ResponseEnum.SUCCESS) {
                    button.setEnabled(true);

                    showAlertDebug();

                    Toast.makeText(LoginActivity.this, responseEnum.getName(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "", Toast.LENGTH_LONG).show();
            return;
        }

        processLogin();
    }

    private void showAlertDebug() {

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
            Toast.makeText(LoginActivity.this, R.string.email_or_password_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.check) {
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
        }
        return super.onOptionsItemSelected(item);
    }
}