package com.kent.university.privelt.events;

import com.kent.university.privelt.model.Card;

public class DetailedCardEvent {

    public Card card;

    public DetailedCardEvent(Card card) {
        this.card = card;
    }
}
