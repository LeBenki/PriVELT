/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.Sensor;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SensorAdapter extends RecyclerView.Adapter<SensorViewHolder> {

    private List<Sensor> sensorList;

    SensorAdapter(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_application, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        holder.bind(sensorList.get(position));
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }
}
