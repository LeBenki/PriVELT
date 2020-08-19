/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card.detailed

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.R
import com.kent.university.privelt.events.LaunchListDataEvent
import com.kent.university.privelt.model.CardItem
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import kotlinx.android.synthetic.main.cell_detailed_card.view.*
import net.neferett.webviewsextractor.model.UserDataTypes
import org.greenrobot.eventbus.EventBus
import java.util.*

class DetailedCardViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(cardItem: CardItem, isService: Boolean) {
        itemView.text1!!.text = SentenceAdapter.adapt(itemView.text1!!.context.resources.getString(R.string.data_found), cardItem.number)
        itemView.setOnClickListener { EventBus.getDefault().post(LaunchListDataEvent(cardItem.name)) }
        if (!isService) {
            val userDataType = UserDataTypes.getUserDataType(cardItem.name.toUpperCase(Locale.ROOT))
            itemView.icon!!.setImageResource(userDataType.res)
        } else {
            val priVELTApplication = itemView.icon!!.context.applicationContext as PriVELTApplication
            itemView.icon!!.setImageResource(priVELTApplication.serviceHelper!!.getResIdWithName(cardItem.name))
        }
    }

}