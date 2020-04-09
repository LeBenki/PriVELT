/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.model

import java.io.Serializable

class Card(var title: String, var isWatched: Boolean, var isService: Boolean, var metrics: List<CardItem>) : Serializable {

    fun getCardItemWithCardIemTitle(title: String): CardItem? {
        for (i in metrics.indices) {
            if (metrics[i].name == title) return metrics[i]
        }
        return null
    }

}