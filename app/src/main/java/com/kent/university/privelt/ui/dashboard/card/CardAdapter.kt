/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.model.Card
import java.util.*


internal class CardAdapter internal constructor() : RecyclerView.Adapter<CardViewHolder>() {
    private var cards: MutableList<Card>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_service, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun updateCards(cards: List<Card>) {
        this.cards = cards.toMutableList()
    }

    fun removeItem(position: Int) {
        cards.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Card, position: Int) {
        cards.add(position, item)
        notifyItemInserted(position)
    }

    fun getData(): MutableList<Card> {
        return cards
    }

    init {
        cards = ArrayList()
    }
}