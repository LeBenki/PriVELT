package com.kent.university.privelt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kent.university.privelt.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.kent.university.privelt.ui.LoginActivity.KEY_WELCOME;
import static com.kent.university.privelt.ui.LoginActivity.PARAM_SERVICE;

public class ServiceFragment extends Fragment {

    private static final int REQUEST_CODE_CONNECTION = 1234;

    @BindView(R.id.connection) Button testButton;
    @BindView(R.id.result) TextView result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        ButterKnife.bind(this, view);

        testButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(ServiceFragment.this.getContext(), LoginActivity.class);
            int code = -1;
            if (String.valueOf(((AppCompatActivity) getActivity()).getSupportActionBar().getTitle()).equals("Google")) {
                code = 0;
            }
            else if (String.valueOf(((AppCompatActivity) getActivity()).getSupportActionBar().getTitle()).equals("Hotels.com")) {
                code = 1;
            }
            else if (String.valueOf(((AppCompatActivity) getActivity()).getSupportActionBar().getTitle()).equals("Strava")) {
                code = 2;
            }
            intent.putExtra(PARAM_SERVICE, code);
            startActivityForResult(intent, REQUEST_CODE_CONNECTION);
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CONNECTION) {
            result.setText(data.getStringExtra(KEY_WELCOME));
        }
    }
}
