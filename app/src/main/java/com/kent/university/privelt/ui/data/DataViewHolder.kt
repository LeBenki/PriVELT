/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.data

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.kent.university.privelt.R
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import net.neferett.webviewsextractor.model.UserDataTypes
import java.util.*

class DataViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    @BindView(R.id.title)
    var title: TextView? = null

    @JvmField
    @BindView(R.id.value)
    var value: TextView? = null

    @JvmField
    @BindView(R.id.concatenated_data)
    var concatenatedData: TextView? = null

    @JvmField
    @BindView(R.id.icon)
    var icon: ImageView? = null
    fun bind(userData: UserData) {
        title!!.text = SentenceAdapter.capitaliseFirstLetter(userData.type)
        value!!.text = userData.value
        concatenatedData!!.text = TextUtils.join("\n", userData.unConcatenatedData)
        val userDataType = UserDataTypes.valueOf(userData.type.toUpperCase(Locale.ROOT))
        icon!!.setImageResource(userDataType.res)
    }

    init {
        ButterKnife.bind(this, itemView)
    }
}