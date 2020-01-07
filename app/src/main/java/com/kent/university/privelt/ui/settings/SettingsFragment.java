package com.kent.university.privelt.ui.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.ui.master_password.MasterPasswordActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    public static final String ARG_CHANGE_PASSWORD = "ARG_CHANGE_PASSWORD";
    private SettingsViewModel shareViewModel;

    @BindView(R.id.text_share)
    TextView textView;

    @BindView(R.id.change_password)
    Button button;

    @BindView(R.id.logout)
    Button logout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, root);
        shareViewModel.getText().observe(this, textView::setText);

        button.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsFragment.this.getActivity(), MasterPasswordActivity.class);
            intent.putExtra(ARG_CHANGE_PASSWORD, true);
            startActivity(intent);
        });

        logout.setOnClickListener(view -> {
            new AlertDialog.Builder(SettingsFragment.this.getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Log out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        startActivity(new Intent(SettingsFragment.this.getActivity(), MasterPasswordActivity.class));
                        SettingsFragment.this.getActivity().finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return root;
    }
}