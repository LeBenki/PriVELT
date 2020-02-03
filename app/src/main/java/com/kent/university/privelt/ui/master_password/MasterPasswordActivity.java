package com.kent.university.privelt.ui.master_password;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.database.PriVELTDatabase;
import com.kent.university.privelt.ui.dashboard.DashboardActivity;
import com.kent.university.privelt.utils.PasswordChecker;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import static com.kent.university.privelt.ui.settings.SettingsActivity.ARG_CHANGE_PASSWORD;
import static com.kent.university.privelt.utils.EyePassword.configureEye;

public class MasterPasswordActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private static final String KEY_MASTER_PASSWORD_ALREADY_GIVEN = "KEY_MASTER_PASSWORD_ALREADY_GIVEN";
    private static final String KEY_SP = "KEY_SP";
    private Zxcvbn zxcvbn;

    private boolean changePassword;

    @BindView(R.id.start)
    Button start;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.confirm_password)
    EditText confirmPassword;

    @BindView(R.id.reset)
    TextView reset;

    @BindView(R.id.hint)
    TextView hint;

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    @BindView(R.id.progress_password)
    ProgressBar progressPassword;

    @BindView(R.id.password_strength)
    TextView strengthView;

    @BindView(R.id.password_meter)
    LinearLayout passwordMeter;

    @BindView(R.id.eye_password)
    ImageView eye;

    @BindView(R.id.eye_confirm_password)
    ImageView eyeConfirm;

    private SharedPreferences mSharedPreferences;
    private boolean masterPasswordAlreadyGiven;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_password);

        ButterKnife.bind(this);

        start.setOnClickListener(this);
        password.addTextChangedListener(this);

        zxcvbn = new Zxcvbn();

        if (getIntent() != null) {
            changePassword = getIntent().getBooleanExtra(ARG_CHANGE_PASSWORD, false);
        }
        else if (savedInstanceState != null) {
            changePassword = savedInstanceState.getBoolean(ARG_CHANGE_PASSWORD, false);
        }

        setTitle("");

        if (changePassword) {
            reset.setVisibility(View.GONE);
            start.setText(getString(R.string.change_master_password));
        }
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mSharedPreferences = getSharedPreferences(KEY_SP, MODE_PRIVATE);
        masterPasswordAlreadyGiven = mSharedPreferences.getBoolean(KEY_MASTER_PASSWORD_ALREADY_GIVEN, false);

        if (!(changePassword || !masterPasswordAlreadyGiven)) {
            passwordMeter.setVisibility(View.GONE);
            hint.setVisibility(View.GONE);
            confirmPassword.setVisibility(View.GONE);
            eyeConfirm.setVisibility(View.GONE);
        }
        else {
            reset.setVisibility(View.GONE);
        }
        resetMasterPassword();

        configureEye(eye, password);
        configureEye(eyeConfirm, confirmPassword);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_CHANGE_PASSWORD, changePassword);
    }

    @SuppressLint("StaticFieldLeak")
    private void resetMasterPassword() {
        reset.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.reset_confirmation)
                .setMessage(R.string.process_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    reset.setEnabled(false);
                    start.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            mSharedPreferences.edit().putBoolean(KEY_MASTER_PASSWORD_ALREADY_GIVEN, false).apply();
                            masterPasswordAlreadyGiven = false;
                            changePassword = false;
                            MasterPasswordActivity.this.deleteDatabase(PriVELTDatabase.PriVELTDatabaseName);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            progressBar.setVisibility(View.GONE);
                            reset.setEnabled(true);
                            start.setEnabled(true);

                            passwordMeter.setVisibility(View.VISIBLE);
                            hint.setVisibility(View.VISIBLE);
                            confirmPassword.setVisibility(View.VISIBLE);
                            reset.setVisibility(View.GONE);
                            eyeConfirm.setVisibility(View.VISIBLE);

                            Toast.makeText(MasterPasswordActivity.this, R.string.reset_done, Toast.LENGTH_LONG).show();
                        }
                    }.execute();
                })
                .setNegativeButton(R.string.no, null)
                .show());
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View view) {
        if (confirmPassword.getVisibility() == View.VISIBLE && !password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(MasterPasswordActivity.this, R.string.different_password, Toast.LENGTH_LONG).show();
            return;
        }
        if (!PasswordChecker.checkPassword(zxcvbn, password.getText().toString())) {
            int errMessage = changePassword || !masterPasswordAlreadyGiven ? R.string.not_respect_policy : R.string.wrong_password;
            Toast.makeText(MasterPasswordActivity.this, errMessage, Toast.LENGTH_LONG).show();
            return;
        }
        start.setEnabled(false);
        reset.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        Editable password = this.password.getText();

        getIdentityManager().setPassword(password);

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... v) {
                if (changePassword || !masterPasswordAlreadyGiven) {
                    if (changePassword) {
                        MasterPasswordActivity.this.getIdentityManager().changePassword(password);
                    }
                    if (!masterPasswordAlreadyGiven)
                        mSharedPreferences.edit().putBoolean(KEY_MASTER_PASSWORD_ALREADY_GIVEN, true).apply();
                    masterPasswordAlreadyGiven = true;
                    return true;
                }
                else {
                    PriVELTDatabase dbHelperObj = PriVELTDatabase.getInstance(MasterPasswordActivity.this);
                    try {
                        dbHelperObj.getOpenHelper().getReadableDatabase();
                    } catch (Exception e) {
                        dbHelperObj.close();
                        PriVELTDatabase.nullDatabase();
                        return false;
                    }
                    return true;
                }
            }

            @Override
            protected void onPostExecute(Boolean res) {
                super.onPostExecute(res);
                start.setEnabled(true);
                reset.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if (res) {
                    if (!changePassword) {
                        startActivity(new Intent(MasterPasswordActivity.this, DashboardActivity.class));
                    }
                    finish();
                }
                else {
                    int errMessage = changePassword ? R.string.same_password : R.string.wrong_password;
                    Toast.makeText(MasterPasswordActivity.this, errMessage, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        updatePasswordStrengthView(editable.toString());
    }

    private void updatePasswordStrengthView(String password) {
        if (TextView.VISIBLE != strengthView.getVisibility())
            return;

        if (password.isEmpty()) {
            strengthView.setText("");
            progressPassword.setProgress(0);
            return;
        }

        Strength strength = zxcvbn.measure(password);

        PasswordStrength ps = PasswordStrength.values()[strength.getScore()];
        progressPassword.setProgress(ps.getProgress());
        strengthView.setText(ps.getResId());
        strengthView.setTextColor(ps.getColor());
        progressPassword.getProgressDrawable().setColorFilter(ps.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
    }
}
