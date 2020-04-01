package com.kent.university.privelt.ui.settings;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.GoogleDriveActivity;
import com.kent.university.privelt.base.GoogleDriveListener;
import com.kent.university.privelt.model.Settings;
import com.kent.university.privelt.ui.master_password.MasterPasswordActivity;

import androidx.annotation.Nullable;
import butterknife.BindView;

public class SettingsActivity extends GoogleDriveActivity {

    public static final String ARG_CHANGE_PASSWORD = "ARG_CHANGE_PASSWORD";

    @BindView(R.id.change_password)
    Button button;

    @BindView(R.id.logout)
    Button logout;

    @BindView(R.id.drive)
    Switch drive;

    @BindView(R.id.fileId)
    EditText fileId;

    @BindView(R.id.googleId)
    TextView googleId;

    @BindView(R.id.disconnect)
    ImageView disconnect;

    SettingsViewModel settingsViewModel;

    private Settings settings;

    @Override
    protected void configureDesign(@Nullable Bundle savedInstanceState) {

        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, MasterPasswordActivity.class);
            intent.putExtra(ARG_CHANGE_PASSWORD, true);
            startActivity(intent);
        });

        logout.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.log_out)
                .setMessage(R.string.log_out_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    startActivity(new Intent(this, MasterPasswordActivity.class));
                    this.finish();
                })
                .setNegativeButton(R.string.no, null)
                .show());

        listener = new GoogleDriveListener() {
            @Override
            public void onDownloadSuccess() {

            }

            @Override
            public void onDownloadFailure() {

            }

            @Override
            public void onConnectionSuccess() {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SettingsActivity.this);

                googleId.setText(getResources().getString(R.string.logged_with, account.getDisplayName()));
                disconnect.setVisibility(View.VISIBLE);
                googleId.setVisibility(View.VISIBLE);
            }
        };

        drive.setOnCheckedChangeListener((compoundButton, b) -> {
            settings.setGoogleDriveAutoSave(b);
            if (b)
                googleDriveConnection();
            settingsViewModel.updateSettings(settings);
        });

        fileId.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied text", fileId.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(SettingsActivity.this, "Text copied to clipboard", Toast.LENGTH_LONG).show();
        });

        disconnect.setOnClickListener(view -> {

            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            googleSignInClient.signOut();

            settings.setGoogleDriveAutoSave(false);
            settingsViewModel.updateSettings(settings);
        });
        configureViewModel();
        getSettings();
    }

    private void getSettings() {
        settingsViewModel.getSettings().observe(this, this::updateSettings);
    }

    private void updateSettings(Settings settings) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        this.settings = settings;
        if (settings == null)
            this.settings = new Settings(false, "");
        drive.setChecked(this.settings.isGoogleDriveAutoSave());
        fileId.setText(this.settings.getGoogleDriveFileID());

        if (account != null) {
            googleId.setText(getResources().getString(R.string.logged_with, account.getDisplayName()));
            disconnect.setVisibility(View.VISIBLE);
            googleId.setVisibility(View.VISIBLE);
        } else {
            this.settings.setGoogleDriveAutoSave(false);
            disconnect.setVisibility(View.GONE);
            googleId.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void configureViewModel() {
        settingsViewModel = getViewModel(SettingsViewModel.class);
        settingsViewModel.init();
    }
}