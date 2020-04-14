/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.login

import android.view.View
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlinx.android.synthetic.main.cell_script.view.*

class ScriptViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(name: String?, scripts: LinkedHashMap<String?, Boolean>) {
        itemView.script.text = name
        itemView.script.isChecked = scripts[name]!!
        itemView.script.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean -> scripts[name] = b }
    }
}