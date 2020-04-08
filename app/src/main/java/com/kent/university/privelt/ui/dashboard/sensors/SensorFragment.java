/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors;

import android.content.Intent;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseFragment;
import com.kent.university.privelt.events.LaunchDetailedSensorEvent;
import com.kent.university.privelt.model.Sensor;
import com.kent.university.privelt.ui.dashboard.sensors.detailed.DetailedSensorActivity;
import com.kent.university.privelt.utils.sensors.SensorHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class SensorFragment extends BaseFragment {

    public static final String PARAM_SENSOR = "PARAM_SENSOR";

    @BindView(R.id.sensors)
    RecyclerView sensors;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_sensors;
    }

    @Override
    protected void configureViewModel() {

    }

    @Override
    protected void configureDesign() {
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        List<Sensor> sensorsList = SensorHelper.getSensorsInformation(getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        sensors.setLayoutManager(layoutManager);
        SensorAdapter cardAdapter = new SensorAdapter(sensorsList);
        sensors.setAdapter(cardAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onDetailedSensorEvent(LaunchDetailedSensorEvent event) {
        Intent intent = new Intent(getActivity(), DetailedSensorActivity.class);
        intent.putExtra(PARAM_SENSOR, event.sensor);
        startActivity(intent);
    }
}
