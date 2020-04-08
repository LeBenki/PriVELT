/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.detailed;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.model.Sensor;
import com.kent.university.privelt.ui.dashboard.card.CardAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import static com.kent.university.privelt.ui.dashboard.sensors.SensorFragment.PARAM_SENSOR;

public class DetailedSensorActivity extends BaseActivity {

    Sensor sensor;

    @BindView(R.id.image_logo)
    ImageView logo;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.recycler_view_applications)
    RecyclerView applications;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_detailed_sensor;
    }

    @Override
    protected void configureViewModel() {

    }

    @Override
    protected void configureDesign(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            sensor = (Sensor) savedInstanceState.getSerializable(PARAM_SENSOR);
        }
        else if (getIntent().getExtras() != null) {
            sensor = (Sensor) getIntent().getExtras().getSerializable(PARAM_SENSOR);
        }

        logo.setImageResource(sensor.getResId());
        title.setText(sensor.getName());

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        applications.setLayoutManager(layoutManager);
        DetailedSensorAdapter applicationsAdapter = new DetailedSensorAdapter(sensor.getApplications());
        applications.setAdapter(applicationsAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PARAM_SENSOR, sensor);
    }
}
