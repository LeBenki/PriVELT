/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card.detailed

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.R
import com.kent.university.privelt.events.LaunchListDataEvent
import com.kent.university.privelt.model.CardItem
import com.kent.university.privelt.utils.sentence.SentenceAdapter
import net.neferett.webviewsextractor.model.UserDataTypes
import org.greenrobot.eventbus.EventBus

class DetailedCardViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    @BindView(R.id.text1)
    var text: TextView? = null

    @JvmField
    @BindView(R.id.icon)
    var imageView: ImageView? = null
    fun bind(cardItem: CardItem, isService: Boolean) {
        text!!.text = SentenceAdapter.adapt(text!!.context.resources.getString(R.string.data_found), cardItem.number)
        text!!.setOnClickListener { view: View? -> EventBus.getDefault().post(LaunchListDataEvent(cardItem.name)) }
        if (!isService) {
            val userDataType = UserDataTypes.valueOf(cardItem.name.toUpperCase())
            imageView!!.setImageResource(userDataType.res)
        } else {
            val priVELTApplication = imageView!!.context.applicationContext as PriVELTApplication
            imageView!!.setImageResource(priVELTApplication.serviceHelper!!.getResIdWithName(cardItem.name))
        }
    }

    init {
        ButterKnife.bind(this, itemView)
    }
}