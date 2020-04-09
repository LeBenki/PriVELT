/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils

import com.kent.university.privelt.model.Card
import com.kent.university.privelt.model.CardItem
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import java.util.*

object CardManager {
    private fun generateCards(userDataList: List<UserData>?, services: List<Service>): List<Card> {
        val cards: MutableList<Card> = ArrayList()
        for ((name) in services) {
            cards.add(Card(name, false, true, ArrayList()))
        }
        if (userDataList == null) return cards
        for ((_, type, _, _, serviceId) in userDataList) {
            val service = getServiceFromIndex(serviceId, services)
            if (containsCard(cards, service!!.name)) {
                val card = getCardWithTitle(cards, service.name)
                var cardItems = card!!.getCardItemWithCardIemTitle(type)
                if (cardItems == null) {
                    cardItems = CardItem(type, 1)
                    card.metrics.add(cardItems)
                } else {
                    cardItems.number = cardItems.number + 1
                }
            }
            if (!containsCard(cards, type)) {
                val cardItems: MutableList<CardItem> = ArrayList()
                cardItems.add(CardItem(service.name, 1))
                cards.add(Card(type, false, false, cardItems))
            } else {
                val card = getCardWithTitle(cards, type)
                var cardItems = card!!.getCardItemWithCardIemTitle(service.name)
                if (cardItems == null) {
                    cardItems = CardItem(service.name, 1)
                    card.metrics.add(cardItems)
                } else {
                    cardItems.number = cardItems.number + 1
                }
            }
        }
        return cards
    }

    private fun containsCard(cards: List<Card>, title: String): Boolean {
        for ((title1) in cards) if (title1 == title) return true
        return false
    }

    private fun getCardWithTitle(cards: List<Card>, title: String): Card? {
        for (i in cards.indices) if (cards[i].title == title) return cards[i]
        return null
    }

    private fun getServiceFromIndex(id: Long, services: List<Service>): Service? {
        for (service in services) if (service.id == id) return service
        return null
    }

    fun cardsFilter(userDataList: List<UserData>?, services: List<Service>, filters: BooleanArray?, watchList: List<String>): List<Card> {
        val cards = generateCards(userDataList, services)
        val filteredCards: MutableList<Card> = ArrayList()
        for (favorite in watchList) for (i in cards.indices) if (cards[i].title == favorite) cards[i].isWatched = true
        for (i in cards.indices) {
            if ((filters == null || filters[1] && cards[i].isService) && (filters == null || !filters[2] || cards[i].isWatched)) filteredCards.add(cards[i])
            if ((filters == null || filters[0] && !cards[i].isService) && (filters == null || !filters[2] || cards[i].isWatched)) filteredCards.add(cards[i])
        }
        return filteredCards
    }
}