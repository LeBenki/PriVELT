package com.kent.university.privelt.ui.settings;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.GoogleDriveActivity;
import com.kent.university.privelt.base.GoogleDriveListener;
import com.kent.university.privelt.ui.master_password.MasterPasswordActivity;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends GoogleDriveActivity {

    public static final String ARG_CHANGE_PASSWORD = "ARG_CHANGE_PASSWORD";

    @BindView(R.id.change_password)
    Button button;

    @BindView(R.id.logout)
    Button logout;

    @BindView(R.id.drive)
    Button drive;

    @BindView(R.id.fileId)
    EditText fileId;

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

        listener = new GoogleDriveListener() {
            @Override
            public void onSaveSuccess(String fileId) {
                SettingsActivity.this.fileId.setText(fileId);
            }

            @Override
            public void onDownloadSuccess() {

            }

            @Override
            public void onSaveFailure() {
                Toast.makeText(SettingsActivity.this, "An error occurred", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDownloadFailure() {

            }
        };

        drive.setOnClickListener(v -> googleDriveConnection(PROCESS_SAVE, ""));

        fileId.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied text", fileId.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(SettingsActivity.this, "Text copied to clipboard", Toast.LENGTH_LONG).show();
        });
    }
}