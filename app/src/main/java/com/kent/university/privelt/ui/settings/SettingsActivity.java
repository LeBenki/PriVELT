package com.kent.university.privelt.ui.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.ui.master_password.MasterPasswordActivity;

import androidx.annotation.NonNull;
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
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(new Intent(this, MasterPasswordActivity.class));
                    this.finish();
                })
                .setNegativeButton("No", null)
                .show());
    }

}