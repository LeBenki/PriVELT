package com.kent.university.privelt.ui.settings;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.GoogleDriveActivity;
import com.kent.university.privelt.base.GoogleDriveListener;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.model.Settings;
import com.kent.university.privelt.ui.master_password.MasterPasswordActivity;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

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

    SettingsViewModel settingsViewModel;

    private Settings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

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

        configureViewModel();
        getSettings();
    }

    private void getSettings() {
        settingsViewModel.getSettings().observe(this, this::updateSettings);
    }

    private void updateSettings(Settings settings) {
        this.settings = settings;
        if (settings == null)
            this.settings = new Settings(false, "");
        drive.setChecked(this.settings.isGoogleDriveAutoSave());
        fileId.setText(this.settings.getGoogleDriveFileID());
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        settingsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel.class);
        settingsViewModel.init();
    }
}