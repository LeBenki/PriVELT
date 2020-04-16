/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.R
import com.kent.university.privelt.model.Service
import java.util.*

class ScriptsAdapter internal constructor(scripts: List<String>, alreadyChecked: List<String>) : RecyclerView.Adapter<ScriptViewHolder>() {
    private val scripts: LinkedHashMap<String?, Boolean> = LinkedHashMap()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScriptViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_script, parent, false)
        return ScriptViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScriptViewHolder, position: Int) {
        holder.bind(scripts.keys.toTypedArray()[position], scripts)
    }

    override fun getItemCount(): Int {
        return scripts.size
    }

    val concatenatedScriptsChecked: String
        get() {
            var result = ""
            for ((key, value) in scripts) {
                if (value) result = result + key + Service.DELIMITER
            }
            return result
        }

    init {
        for (script in scripts) {
            this.scripts[script] = false
        }
        for (script in alreadyChecked) {
            if (script.isNotEmpty()) this.scripts[script] = true
        }
    }
}