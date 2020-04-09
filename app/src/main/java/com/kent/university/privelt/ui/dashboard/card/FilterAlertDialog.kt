/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.kent.university.privelt.R

class FilterAlertDialog internal constructor(private val listener: FilterDialogListener) : DialogFragment() {
    interface FilterDialogListener {
        fun onDialogPositiveClick(selectedItems: BooleanArray?)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        // Set the dialog title
        val sharedPreferences = activity!!.getSharedPreferences(KEY_SHARED, Context.MODE_PRIVATE)
        val checkedItems = getFilters(sharedPreferences)
        builder.setTitle(getString(R.string.choose_cards)) // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.filters, checkedItems) { dialogInterface: DialogInterface?, i: Int, b: Boolean -> checkedItems[i] = b }
                .setPositiveButton(R.string.yes) { dialog: DialogInterface?, id: Int ->
                    listener.onDialogPositiveClick(checkedItems)
                    sharedPreferences.edit().putBoolean(DATA, checkedItems[0]).apply()
                    sharedPreferences.edit().putBoolean(SERVICE, checkedItems[1]).apply()
                    sharedPreferences.edit().putBoolean(WATCH, checkedItems[2]).apply()
                }
        return builder.create()
    }

    companion object {
        private const val DATA = "data"
        private const val SERVICE = "service"
        private const val WATCH = "watch"
        const val KEY_SHARED = "KEY_SHARED"
        fun getFilters(sharedPreferences: SharedPreferences): BooleanArray {
            return booleanArrayOf(
                    sharedPreferences.getBoolean(DATA, true),
                    sharedPreferences.getBoolean(SERVICE, true),
                    sharedPreferences.getBoolean(WATCH, false)
            )
        }
    }

}