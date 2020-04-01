package com.kent.university.privelt.ui.master_password;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.GoogleDriveActivity;
import com.kent.university.privelt.base.GoogleDriveListener;
import com.kent.university.privelt.database.PriVELTDatabase;
import com.kent.university.privelt.ui.dashboard.DashboardActivity;
import com.kent.university.privelt.utils.PasswordChecker;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import static com.kent.university.privelt.ui.settings.SettingsActivity.ARG_CHANGE_PASSWORD;
import static com.kent.university.privelt.utils.EyePassword.configureEye;

public class MasterPasswordActivity extends GoogleDriveActivity implements View.OnClickListener, TextWatcher {

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

    private boolean masterPasswordAlreadyGiven;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main_password;
    }

    @Override
    protected void configureViewModel() {

    }

    @Override
    protected void configureDesign(@Nullable Bundle savedInstanceState) {

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

            start.setText(getString(R.string.change_master_password));
        }
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        masterPasswordAlreadyGiven = getDatabasePath(PriVELTDatabase.PriVELTDatabaseName).exists();

        if (!masterPasswordAlreadyGiven) {
            reset.setText(R.string.import_your_data);
            onDataImported();
        }
        else {
            reset.setText(R.string.reset_data);
            resetMasterPassword();
        }

        if (!(changePassword || !masterPasswordAlreadyGiven)) {
            passwordMeter.setVisibility(View.GONE);
            hint.setVisibility(View.GONE);
            confirmPassword.setVisibility(View.GONE);
            eyeConfirm.setVisibility(View.GONE);
        }

        configureEye(eye, password);
        configureEye(eyeConfirm, confirmPassword);

        listener = new GoogleDriveListener() {

            @Override
            public void onDownloadSuccess() {
                Toast.makeText(MasterPasswordActivity.this, R.string.data_imported_correctly, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                reset.setEnabled(true);
                start.setEnabled(true);

                passwordMeter.setVisibility(View.GONE);
                hint.setVisibility(View.GONE);
                confirmPassword.setVisibility(View.GONE);

                eyeConfirm.setVisibility(View.GONE);
                reset.setText(R.string.reset_data);
                resetMasterPassword();
            }

            @Override
            public void onDownloadFailure() {
                Toast.makeText(MasterPasswordActivity.this, R.string.error_occurred, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionSuccess() {

            }
        };
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

                            eyeConfirm.setVisibility(View.VISIBLE);
                            reset.setText(R.string.import_your_data);
                            onDataImported();

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
                    masterPasswordAlreadyGiven = true;
                    return true;
                }
                else {
                    PriVELTDatabase dbHelperObj = PriVELTDatabase.getInstance(MasterPasswordActivity.this);
                    try {
                        dbHelperObj.serviceDao().getAllServices();
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

    public void onDataImported() {
        reset.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText edittext = new EditText(this);
            edittext.setTextColor(Color.parseColor("#000000"));
            alert.setTitle(R.string.enter_file_id);

            alert.setView(edittext);

            alert.setPositiveButton(R.string.yes, (dialog, whichButton) -> {
                googleDriveConnectionAndDownload(true, edittext.getText().toString());
            });

            alert.show();
        });
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
