/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.master_password

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.model.ServicePDA
import kotlinx.android.synthetic.main.cell_pda.view.*

class ImportDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(servicePDA: ServicePDA) {
        itemView.title.text = servicePDA.title
        itemView.logo.setImageResource(servicePDA.image)
    }
}