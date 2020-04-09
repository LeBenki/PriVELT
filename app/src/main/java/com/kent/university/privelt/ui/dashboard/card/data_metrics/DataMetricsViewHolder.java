/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.card.data_metrics;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.kent.university.privelt.PriVELTApplication;
import com.kent.university.privelt.R;
import com.kent.university.privelt.model.CardItem;

import net.neferett.webviewsextractor.model.UserDataTypes;

public class DataMetricsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.value_metrics)
    TextView metrics;

    @BindView(R.id.image_type)
    ImageView type;

    DataMetricsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(CardItem cardItem, boolean isService) {
        metrics.setText(String.valueOf(cardItem.getNumber()));
        if (!isService) {
            UserDataTypes userDataType = UserDataTypes.valueOf(cardItem.getName().toUpperCase());
            type.setImageResource(userDataType.getRes());
        }
        else {
            PriVELTApplication priVELTApplication = (PriVELTApplication) type.getContext().getApplicationContext();
            type.setImageResource(priVELTApplication.getServiceHelper().getResIdWithName(cardItem.getName()));
        }
    }
}
