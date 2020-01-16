package com.kent.university.privelt.ui.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.kent.university.privelt.R;
import com.kent.university.privelt.ui.master_password.MasterPasswordActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    public static final String ARG_CHANGE_PASSWORD = "ARG_CHANGE_PASSWORD";

    @BindView(R.id.change_password)
    Button button;

    @BindView(R.id.logout)
    Button logout;

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
    }

}