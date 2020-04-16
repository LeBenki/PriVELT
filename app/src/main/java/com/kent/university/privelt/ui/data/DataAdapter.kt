/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.model.UserData
import java.util.*

class DataAdapter internal constructor(userData: ArrayList<UserData>) : RecyclerView.Adapter<DataViewHolder>() {
    private var userData: List<UserData>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_userdata, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(userData[position])
    }

    override fun getItemCount(): Int {
        return userData.size
    }

    fun setUserData(userData: List<UserData>) {
        this.userData = userData
    }

    init {
        this.userData = userData
    }
}