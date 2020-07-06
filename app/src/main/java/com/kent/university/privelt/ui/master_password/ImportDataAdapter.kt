/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.master_password

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.model.ServicePDA

class ImportDataAdapter(private var services: List<ServicePDA>) : RecyclerView.Adapter<ImportDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImportDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_pda, parent, false)
        return ImportDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return services.size
    }

    override fun onBindViewHolder(holder: ImportDataViewHolder, position: Int) {
        holder.bind(services[position])
    }
}