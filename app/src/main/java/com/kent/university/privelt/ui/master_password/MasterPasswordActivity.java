package com.kent.university.privelt.ui.master_password;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.model.Credentials;
import com.kent.university.privelt.ui.dashboard.DashboardActivity;
import com.kent.university.privelt.utils.PasswordChecker;
import com.kent.university.privelt.utils.SimpleHash;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kent.university.privelt.database.PriVELTDatabase.DB_SIZE;
import static com.kent.university.privelt.ui.settings.SettingsFragment.ARG_CHANGE_PASSWORD;

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

    private CredentialsViewModel mCredentialsViewModel;
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

        if (changePassword) {
            reset.setVisibility(View.GONE);
            start.setText("Change master password");
        }

        configureViewModel();

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

        configureEye();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_CHANGE_PASSWORD, changePassword);
    }

    private void resetMasterPassword() {
        reset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                reset.setEnabled(false);
                start.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        mSharedPreferences.edit().putBoolean(KEY_MASTER_PASSWORD_ALREADY_GIVEN, false).apply();
                        masterPasswordAlreadyGiven = false;
                        populateDb();
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

                        Toast.makeText(MasterPasswordActivity.this, "All your data has been deleted please enter a new master password", Toast.LENGTH_LONG).show();
                    }
                }.execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View view) {
        if (confirmPassword.getVisibility() == View.VISIBLE && !password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(MasterPasswordActivity.this, "Passwords are not the same", Toast.LENGTH_LONG).show();
            return;
        }
        if (!PasswordChecker.checkPassword(zxcvbn, password.getText().toString())) {
            String errMessage = changePassword || !masterPasswordAlreadyGiven ? "You did not respect the password policy" : "Wrong password, you can reset all your data to enter a new password";
            Toast.makeText(MasterPasswordActivity.this, errMessage, Toast.LENGTH_LONG).show();
            return;
        }
        start.setEnabled(false);
        reset.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        String password = this.password.getText().toString();
        String hashedPassword = SimpleHash.getHashedPassword(SimpleHash.HashMethod.SHA256, password);
        new AsyncTask<Void, Void, Pair<Boolean, Long>>() {
            @Override
            protected Pair<Boolean, Long> doInBackground(Void... v) {

                if (changePassword || !masterPasswordAlreadyGiven) {
                    if (changePassword) {
                        //check if the old password is the same as before
                        long oldIndex = MasterPasswordActivity.this.getIdentityManager().getMpIndex();
                        Credentials c = mCredentialsViewModel.getCredentialsWithId(oldIndex);
                        if (c.getPassword().equals(hashedPassword)) {
                            return new Pair<>(false, oldIndex);
                        }
                        //remove old password
                        mCredentialsViewModel.updateCredentials(new Credentials(oldIndex, "email" + oldIndex, SimpleHash.getHashedPassword(SimpleHash.HashMethod.SHA256, UUID.randomUUID().toString())));
                    }
                    long index = SimpleHash.calulateIndexOfHash(hashedPassword);
                    Credentials credentials = new Credentials(index, "email" + index, hashedPassword);
                    mCredentialsViewModel.updateCredentials(credentials);
                    if (!masterPasswordAlreadyGiven)
                        mSharedPreferences.edit().putBoolean(KEY_MASTER_PASSWORD_ALREADY_GIVEN, true).apply();
                    masterPasswordAlreadyGiven = true;
                    return new Pair<>(true, index);
                }
                else {
                    long index = SimpleHash.calulateIndexOfHash(hashedPassword);
                    Credentials credentials = mCredentialsViewModel.getCredentialsWithId(index);

                    String oldHash = credentials.getPassword();
                    return new Pair<>(oldHash.equals(hashedPassword), index);
                }
            }

            @Override
            protected void onPostExecute(Pair<Boolean, Long> pair) {
                super.onPostExecute(pair);
                start.setEnabled(true);
                reset.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if (pair.first) {
                    if (changePassword) {
                        finish();
                    }
                    else {
                        startActivity(new Intent(MasterPasswordActivity.this, DashboardActivity.class));
                        MasterPasswordActivity.this.getIdentityManager().setMpIndex(pair.second);
                        finish();
                    }
                }
                else {
                    String errMessage = changePassword ? "The password is the same as before" : "Wrong password, you can reset all your data to enter a new password";
                    Toast.makeText(MasterPasswordActivity.this, errMessage, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        mCredentialsViewModel = ViewModelProviders.of(this, viewModelFactory).get(CredentialsViewModel.class);
    }

    private void populateDb() {
        for (int i = 0; i < DB_SIZE; i++) {
            mCredentialsViewModel.updateCredentials(new Credentials(i, "email" + i, SimpleHash.getHashedPassword(SimpleHash.HashMethod.SHA256, UUID.randomUUID().toString())));
        }
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

    @SuppressLint("ClickableViewAccessibility")
    private void configureEye() {
        eye.setOnTouchListener((v, event) -> showEye(event, password));
        eyeConfirm.setOnTouchListener((v, event) -> showEye(event, confirmPassword));
    }

    private boolean showEye(MotionEvent event, EditText editText) {
        switch (event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case MotionEvent.ACTION_UP:
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }
        return true;
    }
}
