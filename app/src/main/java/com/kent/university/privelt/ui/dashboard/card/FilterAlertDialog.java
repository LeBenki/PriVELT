/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.card;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.kent.university.privelt.R;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class FilterAlertDialog extends DialogFragment {

    private static final String DATA = "data";
    private static final String SERVICE = "service";
    private static final String WATCH = "watch";
    public static final String KEY_SHARED = "KEY_SHARED";

    FilterAlertDialog(FilterDialogListener listener) {
        this.listener = listener;
    }

    public interface FilterDialogListener {
        void onDialogPositiveClick(boolean[] selectedItems);
    }

    private FilterDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(KEY_SHARED, Context.MODE_PRIVATE);

        boolean[] checkedItems = getFilters(sharedPreferences);

        builder.setTitle(getString(R.string.choose_cards))
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.filters, checkedItems, (dialogInterface, i, b) -> checkedItems[i] = b)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    listener.onDialogPositiveClick(checkedItems);
                    sharedPreferences.edit().putBoolean(DATA, checkedItems[0]).apply();
                    sharedPreferences.edit().putBoolean(SERVICE, checkedItems[1]).apply();
                    sharedPreferences.edit().putBoolean(WATCH, checkedItems[2]).apply();
                });

        return builder.create();
    }

    public static boolean[] getFilters(SharedPreferences sharedPreferences) {
        return new boolean[]{
                sharedPreferences.getBoolean(DATA, true),
                sharedPreferences.getBoolean(SERVICE, true),
                sharedPreferences.getBoolean(WATCH, false)
        };
    }
}
