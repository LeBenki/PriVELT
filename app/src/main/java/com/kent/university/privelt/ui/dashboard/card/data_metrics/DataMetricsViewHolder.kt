/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card.data_metrics

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.R
import com.kent.university.privelt.model.CardItem
import net.neferett.webviewsextractor.model.UserDataTypes

class DataMetricsViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    @BindView(R.id.value_metrics)
    var metrics: TextView? = null

    @JvmField
    @BindView(R.id.image_type)
    var type: ImageView? = null
    fun bind(cardItem: CardItem, isService: Boolean) {
        metrics!!.text = cardItem.number.toString()
        if (!isService) {
            val userDataType = UserDataTypes.valueOf(cardItem.name.toUpperCase())
            type!!.setImageResource(userDataType.res)
        } else {
            val priVELTApplication = type!!.context.applicationContext as PriVELTApplication
            type!!.setImageResource(priVELTApplication.serviceHelper!!.getResIdWithName(cardItem.name))
        }
    }

    init {
        ButterKnife.bind(this, itemView)
    }
}