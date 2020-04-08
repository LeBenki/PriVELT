/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.card.detailed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.CardItem;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DetailedCardAdapter extends RecyclerView.Adapter<DetailedCardViewHolder> {

    private List<CardItem> cardItems;
    private boolean isService;

    DetailedCardAdapter(List<CardItem> objects, boolean isService) {
        cardItems = objects;
        this.isService = isService;
    }

    @NonNull
    @Override
    public DetailedCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_detailed_card, parent, false);
        return new DetailedCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedCardViewHolder holder, int position) {
        holder.bind(cardItems.get(position), isService);
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }
}
