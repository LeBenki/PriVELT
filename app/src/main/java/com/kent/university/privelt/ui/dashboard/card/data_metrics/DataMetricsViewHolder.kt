/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card.data_metrics

import android.view.View

import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.model.CardItem
import kotlinx.android.synthetic.main.cell_metrics.view.*
import net.neferett.webviewsextractor.model.UserDataTypes

class DataMetricsViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(cardItem: CardItem, isService: Boolean) {
        itemView.value_metrics!!.text = cardItem.number.toString()
        if (!isService) {
            val userDataType = UserDataTypes.valueOf(cardItem.name.toUpperCase())
            itemView.image_type!!.setImageResource(userDataType.res)
        } else {
            val priVELTApplication = itemView.image_type!!.context.applicationContext as PriVELTApplication
            itemView.image_type!!.setImageResource(priVELTApplication.serviceHelper!!.getResIdWithName(cardItem.name))
        }
    }
}