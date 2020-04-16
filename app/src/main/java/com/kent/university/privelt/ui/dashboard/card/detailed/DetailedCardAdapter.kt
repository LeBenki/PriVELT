/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card.detailed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.model.CardItem

class DetailedCardAdapter internal constructor(private val cardItems: List<CardItem>, private val isService: Boolean) : RecyclerView.Adapter<DetailedCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailedCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_detailed_card, parent, false)
        return DetailedCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailedCardViewHolder, position: Int) {
        holder.bind(cardItems[position], isService)
    }

    override fun getItemCount(): Int {
        return cardItems.size
    }

}