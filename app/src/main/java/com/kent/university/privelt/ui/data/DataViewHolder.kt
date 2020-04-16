/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.data

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import net.neferett.webviewsextractor.model.UserDataTypes
import java.util.*
import kotlinx.android.synthetic.main.cell_userdata.view.*

class DataViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(userData: UserData) {
        itemView.title!!.text = SentenceAdapter.capitaliseFirstLetter(userData.type)
        itemView.value!!.text = userData.value
        itemView.concatenated_data!!.text = TextUtils.join("\n", userData.unConcatenatedData)
        val userDataType = UserDataTypes.valueOf(userData.type.toUpperCase(Locale.ROOT))
        itemView.icon!!.setImageResource(userDataType.res)
    }
}