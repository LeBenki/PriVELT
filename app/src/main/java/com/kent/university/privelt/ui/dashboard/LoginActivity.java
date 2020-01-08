package com.kent.university.privelt.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.webviewautologin.response.ResponseCallback;
import com.kent.university.webviewautologin.response.ResponseEnum;
import com.kent.university.webviewautologin.services.LoginService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String PARAM_SHOULD_STORE = "PARAM_SHOULD_STORE";
    public static final String PARAM_USER = "PARAM_USER";
    public static final String PARAM_PASSWORD = "PARAM_PASSWORD";

    public static final String PARAM_SERVICE = "PARAM_SERVICE";
    private String service;

    @BindView(R.id.best_poc) Button button;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.progress_circular) ProgressBar progressBar;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.debug) Button debug;

    LoginService loginService = null;
    AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        button.setOnClickListener(this);

        if (getIntent() != null) {
            service = getIntent().getStringExtra(PARAM_SERVICE);
        }
        else if (savedInstanceState != null) {
            service = savedInstanceState.getString(PARAM_SERVICE);
        }

        title.setText(service);

        debug.setOnClickListener(view -> showAlertDebug());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAM_SERVICE, service);
    }

    public void processLogin(boolean shouldStorePassword) {

        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);

        loginService = getServiceHelper().getServiceWithName(service);

        String user = email.getText().toString();
        String pass = password.getText().toString();

        loginService.autoLogin(email.getText().toString(), password.getText().toString(), new ResponseCallback() {
            @Override
            public void getResponse(ResponseEnum responseEnum, String data) {
                if (responseEnum != ResponseEnum.SUCCESS) {
                    button.setEnabled(true);

                    showAlertDebug();

                    Toast.makeText(LoginActivity.this, responseEnum.getName(), Toast.LENGTH_LONG).show();

                } else {
                    Intent intent = new Intent();
                    intent.putExtra(PARAM_SHOULD_STORE, shouldStorePassword);
                    intent.putExtra(PARAM_USER, user);
                    intent.putExtra(PARAM_PASSWORD, pass);
                    intent.putExtra(PARAM_SERVICE, service);
                    setResult(RESULT_OK, intent);
                    finish();
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

        new android.app.AlertDialog.Builder(LoginActivity.this)
                .setTitle("Unsubscribe")
                .setMessage("Are you sure you want to unsubscribe to this service?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    processLogin(true);
                })
                .setNegativeButton("No", (dialogInterface, i) -> processLogin(false))
                .show();
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
}