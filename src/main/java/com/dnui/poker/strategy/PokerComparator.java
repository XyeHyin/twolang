package com.dnui.poker.strategy;

import java.util.*;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24
 * @Description: 德州扑克比牌逻辑（支持德州扑克常见牌型：皇家同花顺>同花顺>四条>葫芦>同花>顺子>三条>两对>一对>高牌）
 * @Version: 1.0
 */
public class PokerComparator {
    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(11), QUEEN(12), KING(13), ACE(14);
        public final int value;
        Rank(int value) { this.value = value; }
    }
    public static class Card {
        public final Suit suit;
        public final Rank rank;
        public Card(Suit suit, Rank rank) { this.suit = suit; this.rank = rank; }
        public String toString() {
            return rank.name() + suit.name().charAt(0);
        }
    }
    public enum HandType {
        ROYAL_FLUSH, STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH,
        STRAIGHT, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD
    }
    public static class HandResult implements Comparable<HandResult> {
        public final HandType type;
        public final List<Integer> ranks;
        public HandResult(HandType type, List<Integer> ranks) {
            this.type = type; this.ranks = ranks;
        }
        @Override
        public int compareTo(HandResult o) {
            if (this.type != o.type) return this.type.ordinal() - o.type.ordinal();
            for (int i = 0; i < this.ranks.size(); i++) {
                if (i >= o.ranks.size()) return 1;
                int cmp = this.ranks.get(i) - o.ranks.get(i);
                if (cmp != 0) return cmp;
            }
            return 0;
        }
    }
}