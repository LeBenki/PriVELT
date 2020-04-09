/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.login

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.kent.university.privelt.R
import java.util.*

class ScriptViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    @BindView(R.id.check_script)
    var script: CheckBox? = null
    fun bind(name: String?, scripts: LinkedHashMap<String?, Boolean>) {
        script!!.text = name
        script!!.isChecked = scripts[name]!!
        script!!.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean -> scripts[name] = b }
    }

    init {
        ButterKnife.bind(this, itemView)
    }
}