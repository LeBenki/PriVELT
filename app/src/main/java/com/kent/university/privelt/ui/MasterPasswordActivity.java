package com.kent.university.privelt.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.kent.university.privelt.utils.SimpleHash;

import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kent.university.privelt.database.PriVELTDatabase.DB_SIZE;

public class MasterPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_MASTER_PASSWORD_ALREADY_GIVEN = "KEY_MASTER_PASSWORD_ALREADY_GIVEN";
    private static final String KEY_SP = "KEY_SP";

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

        configureViewModel();

        mSharedPreferences = getSharedPreferences(KEY_SP, MODE_PRIVATE);
        masterPasswordAlreadyGiven = mSharedPreferences.getBoolean(KEY_MASTER_PASSWORD_ALREADY_GIVEN, false);

        resetMasterPassword();
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
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... v) {

                if (!masterPasswordAlreadyGiven) {
                    long index = SimpleHash.calulateIndexOfHash(hashedPassword);
                    Credentials credentials = new Credentials(index, "email" + index, hashedPassword);
                    mCredentialsViewModel.updateCredentials(credentials);
                    mSharedPreferences.edit().putBoolean(KEY_MASTER_PASSWORD_ALREADY_GIVEN, true).apply();
                    masterPasswordAlreadyGiven = true;
                    return true;
                }
                else {
                    long index = SimpleHash.calulateIndexOfHash(hashedPassword);
                    Credentials credentials = mCredentialsViewModel.getCredentialsWithId(index);

                    String oldHash = credentials.getPassword();
                    return oldHash.equals(hashedPassword);
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                start.setEnabled(true);
                reset.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if (aBoolean) {
                    startActivity(new Intent(MasterPasswordActivity.this, MainActivity.class));
                    finish();
                }
                else
                    Toast.makeText(MasterPasswordActivity.this, "Wrong password, you can reset all your data to enter a new password", Toast.LENGTH_LONG).show();
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
}
