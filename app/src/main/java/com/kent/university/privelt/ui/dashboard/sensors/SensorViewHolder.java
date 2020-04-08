/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.events.DetailedCardEvent;
import com.kent.university.privelt.events.LaunchDetailedSensorEvent;
import com.kent.university.privelt.model.Sensor;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class SensorViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_sensor)
    ImageView image;

    @BindView(R.id.sensor_value)
    TextView number;

    @BindView(R.id.sensor_status_name)
    TextView statusName;

    @BindView(R.id.sensor_status_image)
    View statusImage;

    SensorViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bind(Sensor sensor) {
        image.setImageResource(sensor.getResId());
        number.setText(String.valueOf(sensor.getApplications().size()));
        itemView.setOnClickListener(view -> EventBus.getDefault().post(new LaunchDetailedSensorEvent(sensor)));

        if (sensor.isSensor()) {
            statusImage.setVisibility(View.VISIBLE);
            statusName.setVisibility(View.VISIBLE);
            boolean isEnabled = sensor.isEnabled(itemView.getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusImage.setBackgroundTintList(ColorStateList.valueOf(isEnabled ? Color.RED : Color.GREEN));
            }
            statusName.setText(isEnabled ? "On" : "Off");
        }
        else {
            statusImage.setVisibility(View.GONE);
            statusName.setVisibility(View.GONE);
        }
    }
}
