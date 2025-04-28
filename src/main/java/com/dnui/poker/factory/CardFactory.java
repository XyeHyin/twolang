package com.dnui.poker.factory;

import com.dnui.poker.strategy.PokerComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * 扑克牌工厂，支持标准牌和短牌
 */
public abstract class CardFactory {
    public abstract List<PokerComparator.Card> createDeck();

    /**
     * 标准52张扑克牌工厂
     */
    public static class StandardDeckFactory extends CardFactory {
        @Override
        public List<PokerComparator.Card> createDeck() {
            List<PokerComparator.Card> deck = new ArrayList<>();
            for (PokerComparator.Suit suit : PokerComparator.Suit.values()) {
                for (PokerComparator.Rank rank : PokerComparator.Rank.values()) {
                    deck.add(new PokerComparator.Card(suit, rank));
                }
            }
            return deck;
        }
    }

    /**
     * 短牌（去掉2~5）36张扑克牌工厂
     */
    public static class ShortDeckFactory extends CardFactory {
        @Override
        public List<PokerComparator.Card> createDeck() {
            List<PokerComparator.Card> deck = new ArrayList<>();
            for (PokerComparator.Suit suit : PokerComparator.Suit.values()) {
                for (PokerComparator.Rank rank : PokerComparator.Rank.values()) {
                    if (rank.value >= 6 || rank == PokerComparator.Rank.ACE) {
                        deck.add(new PokerComparator.Card(suit, rank));
                    }
                }
            }
            return deck;
        }
    }
}
