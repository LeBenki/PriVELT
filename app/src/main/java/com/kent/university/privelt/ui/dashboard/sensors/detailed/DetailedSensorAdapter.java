/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.detailed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DetailedSensorAdapter extends RecyclerView.Adapter<DetailedSensorViewHolder> {

    private List<Application> applicationList;

    public DetailedSensorAdapter(List<Application> applicationList) {
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public DetailedSensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_detailed_sensor, parent, false);
        return new DetailedSensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedSensorViewHolder holder, int position) {
        holder.bind(applicationList.get(position));
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }
}
