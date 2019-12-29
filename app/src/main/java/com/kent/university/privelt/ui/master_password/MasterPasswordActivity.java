package com.kent.university.privelt.ui.master_password;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.model.Credentials;
import com.kent.university.privelt.ui.MainActivity;
import com.kent.university.privelt.utils.SimpleHash;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kent.university.privelt.database.PriVELTDatabase.DB_SIZE;
import static com.kent.university.privelt.ui.settings.SettingsFragment.ARG_CHANGE_PASSWORD;

public class MasterPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_MASTER_PASSWORD_ALREADY_GIVEN = "KEY_MASTER_PASSWORD_ALREADY_GIVEN";
    private static final String KEY_SP = "KEY_SP";

    private boolean changePassword;

    @BindView(R.id.start)
    Button start;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.reset)
    TextView reset;

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    private CredentialsViewModel mCredentialsViewModel;
    private SharedPreferences mSharedPreferences;
    private boolean masterPasswordAlreadyGiven;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_password);

        ButterKnife.bind(this);

        start.setOnClickListener(this);

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

        resetMasterPassword();
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
                        Toast.makeText(MasterPasswordActivity.this, "All your data has been deleted please enter a new master password", Toast.LENGTH_LONG).show();
                    }
                }.execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View view) {
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
                        startActivity(new Intent(MasterPasswordActivity.this, MainActivity.class));
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
        mCredentialsViewModel.setChangePassword(changePassword);
    }

    private void populateDb() {
        for (int i = 0; i < DB_SIZE; i++) {
            mCredentialsViewModel.updateCredentials(new Credentials(i, "email" + i, SimpleHash.getHashedPassword(SimpleHash.HashMethod.SHA256, UUID.randomUUID().toString())));
        }
    }
}
