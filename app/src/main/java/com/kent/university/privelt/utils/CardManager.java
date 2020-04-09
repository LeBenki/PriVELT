/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils;

import com.kent.university.privelt.model.Card;
import com.kent.university.privelt.model.CardItem;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;

import java.util.ArrayList;
import java.util.List;

public class CardManager {

    private static List<Card> generateCards(List<UserData> userDataList, List<Service> services) {

        List<Card> cards = new ArrayList<>();

        for (Service service : services) {
            cards.add(new Card(service.getName(), false, true, new ArrayList<>()));
        }

        if (userDataList == null)
            return cards;

        for (UserData userData : userDataList) {
            Service service = getServiceFromIndex(userData.getServiceId(), services);
            if (containsCard(cards, service.getName())) {
                Card card = getCardWithTitle(cards, service.getName());
                CardItem cardItems = card.getCardItemWithCardIemTitle(userData.getType());
                if (cardItems == null) {
                    cardItems = new CardItem(userData.getType(), 1);
                    card.getMetrics().add(cardItems);
                }
                else {
                    cardItems.setNumber(cardItems.getNumber() + 1);
                }
            }

            if (!containsCard(cards, userData.getType())) {
                List<CardItem> cardItems = new ArrayList<>();
                cardItems.add(new CardItem(service.getName(), 1));
                cards.add(new Card(userData.getType(), false, false, cardItems));
            } else {
                Card card = getCardWithTitle(cards, userData.getType());
                CardItem cardItems = card.getCardItemWithCardIemTitle(service.getName());
                if (cardItems == null) {
                    cardItems = new CardItem(service.getName(), 1);
                    card.getMetrics().add(cardItems);
                }
                else {
                    cardItems.setNumber(cardItems.getNumber() + 1);
                }
            }
        }
        return cards;
    }

    private static boolean containsCard(List<Card> cards, String title) {
        for (Card card: cards)
            if (card.getTitle().equals(title))
                return true;
        return false;
    }

    private static Card getCardWithTitle(List<Card> cards, String title) {
        for (int i = 0; i < cards.size(); i++)
            if (cards.get(i).getTitle().equals(title))
                return cards.get(i);
         return null;
    }

    private static Service getServiceFromIndex(long id, List<Service> services) {
        for (Service service : services)
            if (service.id == id)
                return service;
        return null;
    }

    public static List<Card> cardsFilter(List<UserData> userDataList, List<Service> services,  boolean[] filters, List<String> watchList) {
        List<Card> cards = generateCards(userDataList, services);
        List<Card> filteredCards = new ArrayList<>();

        for (String favorite : watchList)
            for (int i = 0; i < cards.size(); i++)
                if (cards.get(i).getTitle().equals(favorite))
                    cards.get(i).setWatched(true);

        for (int i = 0; i < cards.size(); i++) {
            if ((filters == null || filters[1] && cards.get(i).isService()) && (filters == null || (!filters[2] || cards.get(i).isWatched())))
                filteredCards.add(cards.get(i));
            if ((filters == null || filters[0] && !cards.get(i).isService()) && (filters == null || (!filters[2] || cards.get(i).isWatched())))
                filteredCards.add(cards.get(i));
        }
        return filteredCards;
    }
}
