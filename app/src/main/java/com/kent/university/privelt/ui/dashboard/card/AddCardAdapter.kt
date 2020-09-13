/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.R
import kotlinx.android.synthetic.main.cell_pda.view.*

class AddCardAdapter(private val list: List<String>, private val listener: AddCardDialogFragment.AddCardDialogListener): RecyclerView.Adapter<AddCardAdapter.AddCardViewHolder>() {

    class AddCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind (service: String) {
            itemView.title.text = service
            val priVELTApplication = itemView.context.applicationContext as PriVELTApplication
            itemView.logo!!.setImageResource(priVELTApplication.serviceHelper!!.getResIdWithName(service))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_pda, parent, false)
        return AddCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddCardViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener { listener.onAddServiceClick(list[position]) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}