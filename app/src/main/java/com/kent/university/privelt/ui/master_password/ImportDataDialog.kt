/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.master_password

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kent.university.privelt.R
import com.kent.university.privelt.model.ServicePDA
import kotlinx.android.synthetic.main.alert_import_data.view.*

class ImportDataDialog : DialogFragment() {

    var listener: ImportDataAdapter.ImportDataListener? = null

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        listener = try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            context as ImportDataAdapter.ImportDataListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement ImportDataListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.alert_import_data, null)
            val listServices = ArrayList<ServicePDA>()
            listServices.add(ServicePDA("Google", R.drawable.googlelogo))
            listServices.add(ServicePDA("HAT", R.drawable.hat))

            view.servicesList.setHasFixedSize(true)
            view.servicesList.layoutManager = LinearLayoutManager(context)
            view.servicesList.adapter = ImportDataAdapter(listServices, listener!!)

            builder.setView(view)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}