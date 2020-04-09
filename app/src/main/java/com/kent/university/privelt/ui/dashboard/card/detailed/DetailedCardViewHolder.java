/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.card.detailed;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kent.university.privelt.PriVELTApplication;
import com.kent.university.privelt.R;
import com.kent.university.privelt.events.LaunchListDataEvent;
import com.kent.university.privelt.model.CardItem;
import com.kent.university.privelt.utils.sentence.SentenceAdapter;

import net.neferett.webviewsextractor.model.UserDataTypes;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedCardViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text1)
    TextView text;

    @BindView(R.id.icon)
    ImageView imageView;

    DetailedCardViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(CardItem cardItem, boolean isService) {
        text.setText(SentenceAdapter.adapt(text.getContext().getResources().getString(R.string.data_found), cardItem.getNumber()));
        text.setOnClickListener(view -> EventBus.getDefault().post(new LaunchListDataEvent(cardItem.getName())));

        if (!isService) {
            UserDataTypes userDataType = UserDataTypes.valueOf(cardItem.getName().toUpperCase());
            imageView.setImageResource(userDataType.getRes());
        }
        else {
            PriVELTApplication priVELTApplication = (PriVELTApplication) imageView.getContext().getApplicationContext();
            imageView.setImageResource(priVELTApplication.getServiceHelper().getResIdWithName(cardItem.getName()));
        }
    }
}
