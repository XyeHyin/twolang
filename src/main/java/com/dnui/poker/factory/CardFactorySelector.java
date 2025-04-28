package com.dnui.poker.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CardFactorySelector {

    @Value("${poker.deck-type:standard}")
    private String deckType;

    public CardFactory selectFactory() {
        if ("short".equalsIgnoreCase(deckType)) {
            return new CardFactory.ShortDeckFactory();
        }
        return new CardFactory.StandardDeckFactory();
    }
}