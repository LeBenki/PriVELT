/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.sensors.detailed;

import android.view.View;
import android.widget.TextView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.Application;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class DetailedSensorViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    TextView title;

    DetailedSensorViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bind(Application application) {
        title.setText(application.getName());
    }
}
