package com.kent.university.privelt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.webviewautologin.response.ResponseCallback;
import com.kent.university.webviewautologin.response.ResponseEnum;
import com.kent.university.webviewautologin.services.GoogleService;
import com.kent.university.webviewautologin.services.HotelsComService;
import com.kent.university.webviewautologin.services.LoginService;
import com.kent.university.webviewautologin.services.StravaService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String KEY_WELCOME = "KEY_WELCOME";
    public static final String PARAM_SERVICE = "PARAM_SERVICE";

    @BindView(R.id.best_poc) Button button;
    //@BindView(R.id.result) TextView result;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.progress_circular) ProgressBar progressBar;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.debug) Button debug;

    private int service = -1;
    private final static int GOOGLE = 0;
    private final static int HOTELS = 1;
    private final static int STRAVA = 2;

    private final static String[] titles = {"GOOGLE", "HOTELS.COM", "STRAVA"};
    LoginService loginService = null;
    AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        button.setOnClickListener(this);

        if (getIntent() != null) {
            service = getIntent().getIntExtra(PARAM_SERVICE, service);
        }
        else if (savedInstanceState != null) {
            service = savedInstanceState.getInt(PARAM_SERVICE);
        }

        title.setText(titles[service]);

        debug.setOnClickListener(view -> showAlertDebug());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PARAM_SERVICE, service);
    }

    @Override
    public void onClick(View view) {

        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter an email with a password", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);

        switch (service) {
            case GOOGLE:
                loginService = new GoogleService(LoginActivity.this);
                break;
            case HOTELS:
                loginService = new HotelsComService(LoginActivity.this);
                break;
            case STRAVA:
                loginService = new StravaService(LoginActivity.this);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + service);
        }

        loginService.autoLogin(email.getText().toString(), password.getText().toString(), new ResponseCallback() {
            @Override
            public void getResponse(ResponseEnum responseEnum, String data) {
                Log.d("TIFFANY", "Notre response Enum: " + responseEnum.getName());
                if (responseEnum != ResponseEnum.SUCCESS) {
                    button.setEnabled(true);

                    showAlertDebug();

                    Toast.makeText(LoginActivity.this, responseEnum.getName(), Toast.LENGTH_LONG).show();

                } else {
                    Intent intent = new Intent();
                    if (data.equals("{}"))
                        data = "Successfully connected";
                    intent.putExtra(KEY_WELCOME, data);
                    setResult(RESULT_OK, intent);
                    finish();
                    //result.setText(data);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDebug() {

        if (loginService == null) {
            Toast.makeText(LoginActivity.this, "Login service is not instancied yet", Toast.LENGTH_LONG).show();
            return;
        }

        if (loginService.getWebView().getParent() != null) {
            ((ViewGroup)loginService.getWebView().getParent()).removeView(loginService.getWebView());
        }

        if (alertDialog == null || ! alertDialog.isShowing()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
            dialogBuilder.setView(loginService.getWebView());
            alertDialog = dialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
        }

    }
}