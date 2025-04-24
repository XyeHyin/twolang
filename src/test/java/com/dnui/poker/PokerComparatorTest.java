package com.dnui.poker;

import com.dnui.poker.strategy.PokerComparator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PokerComparatorTest {

    @Test
    void testCompare() {
        PokerComparator comparator = new PokerComparator();

        // 玩家A: 手牌A♠ K♠
        List<PokerComparator.Card> handA = Arrays.asList(
                new PokerComparator.Card(PokerComparator.Suit.SPADES, PokerComparator.Rank.ACE),
                new PokerComparator.Card(PokerComparator.Suit.SPADES, PokerComparator.Rank.KING));
        // 玩家B: 手牌Q♠ J♠
        List<PokerComparator.Card> handB = Arrays.asList(
                new PokerComparator.Card(PokerComparator.Suit.SPADES, PokerComparator.Rank.QUEEN),
                new PokerComparator.Card(PokerComparator.Suit.SPADES, PokerComparator.Rank.JACK));
        // 公共牌: 10♠ 9♠ 8♠ 2♦ 3♣
        List<PokerComparator.Card> pool = Arrays.asList(
                new PokerComparator.Card(PokerComparator.Suit.SPADES, PokerComparator.Rank.TEN),
                new PokerComparator.Card(PokerComparator.Suit.SPADES, PokerComparator.Rank.NINE),
                new PokerComparator.Card(PokerComparator.Suit.SPADES, PokerComparator.Rank.EIGHT),
                new PokerComparator.Card(PokerComparator.Suit.DIAMONDS, PokerComparator.Rank.TWO),
                new PokerComparator.Card(PokerComparator.Suit.CLUBS, PokerComparator.Rank.THREE));

        // 玩家A应为同花顺（A-K-Q-J-10），玩家B为同花顺（Q-J-10-9-8），A胜
        int result = comparator.compare(handA, handB, pool);
        assertEquals(1, result);

        // 交换手牌，B胜
        result = comparator.compare(handB, handA, pool);
        assertEquals(-1, result);

        // 两个玩家手牌都无关紧要，公共牌最大为高牌，平局
        List<PokerComparator.Card> handC = Arrays.asList(
                new PokerComparator.Card(PokerComparator.Suit.HEARTS, PokerComparator.Rank.FOUR),
                new PokerComparator.Card(PokerComparator.Suit.CLUBS, PokerComparator.Rank.FIVE));
        List<PokerComparator.Card> handD = Arrays.asList(
                new PokerComparator.Card(PokerComparator.Suit.DIAMONDS, PokerComparator.Rank.SIX),
                new PokerComparator.Card(PokerComparator.Suit.HEARTS, PokerComparator.Rank.SEVEN));
        List<PokerComparator.Card> pool2 = Arrays.asList(
                new PokerComparator.Card(PokerComparator.Suit.SPADES, PokerComparator.Rank.ACE),
                new PokerComparator.Card(PokerComparator.Suit.HEARTS, PokerComparator.Rank.KING),
                new PokerComparator.Card(PokerComparator.Suit.CLUBS, PokerComparator.Rank.QUEEN),
                new PokerComparator.Card(PokerComparator.Suit.DIAMONDS, PokerComparator.Rank.JACK),
                new PokerComparator.Card(PokerComparator.Suit.SPADES, PokerComparator.Rank.NINE));
        result = comparator.compare(handC, handD, pool2);
        assertEquals(0, result);
    }
}