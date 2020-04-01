/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.login;

import android.view.View;
import android.widget.CheckBox;

import com.kent.university.privelt.R;

import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ScriptViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.check_script)
    CheckBox script;

    ScriptViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String name, LinkedHashMap<String, Boolean> scripts) {
        script.setText(name);
        script.setChecked(scripts.get(name));
        script.setOnCheckedChangeListener((compoundButton, b) -> scripts.put(name, b));
    }
}
