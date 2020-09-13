/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.card

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kent.university.privelt.R
import kotlinx.android.synthetic.main.alert_import_data.view.*

class AddCardDialogFragment(private val serviceList: List<String>, private val listener: AddCardDialogListener) : DialogFragment() {


    interface AddCardDialogListener {
        fun onAddServiceClick(service: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.alert_import_data, null)

            view.servicesList.setHasFixedSize(true)
            view.servicesList.layoutManager = LinearLayoutManager(context)
            view.servicesList.adapter = AddCardAdapter(serviceList, listener)

            builder.setView(view)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}