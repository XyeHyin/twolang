package com.dnui.poker.strategy;

import java.util.*;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24
 * @Description: 德州扑克比牌逻辑（支持德州扑克常见牌型：皇家同花顺>同花顺>四条>葫芦>同花>顺子>三条>两对>一对>高牌）
 * @Version: 1.0
 */
public class PokerComparator {

    public enum Suit {
        //CLUBS: 梅花,DIAMONDS: 方块,HEARTS: 红桃,SPADES: 黑桃
        CLUBS, DIAMONDS, HEARTS, SPADES
    }

    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(11), QUEEN(12), KING(13), ACE(14);

        public final int value;

        Rank(int value) {
            this.value = value;
        }
    }

    public static class Card {
        public final Suit suit;
        public final Rank rank;

        public Card(Suit suit, Rank rank) {
            this.suit = suit;
            this.rank = rank;
        }
    }

    public enum HandType {
        ROYAL_FLUSH, STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH,
        STRAIGHT, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD
    }

    public static class HandResult implements Comparable<HandResult> {
        public final HandType type;
        public final List<Integer> ranks; // 用于比较大小，降序排列

        public HandResult(HandType type, List<Integer> ranks) {
            this.type = type;
            this.ranks = ranks;
        }

        @Override
        public int compareTo(HandResult o) {
            if (this.type != o.type) {
                return this.type.ordinal() - o.type.ordinal();
            }
            for (int i = 0; i < this.ranks.size(); i++) {
                if (i >= o.ranks.size()) return 1;
                int cmp = this.ranks.get(i) - o.ranks.get(i);
                if (cmp != 0) return cmp;
            }
            return 0;
        }
    }

    /**
     * 比较两手牌，返回1表示handA赢，-1表示handB赢，0表示平局
     */
    public int compare(List<Card> handA, List<Card> handB, List<Card> pool) {
        HandResult resultA = evaluateBestHand(handA, pool);
        HandResult resultB = evaluateBestHand(handB, pool);
        int cmp = resultA.compareTo(resultB);
        if (cmp > 0)  return 1;
        if (cmp < 0)  return -1;
        return 0;
    }

    /**
     * 计算玩家7张牌（2手牌+5公牌）中最大的5张组合
     */
    public HandResult evaluateBestHand(List<Card> hand, List<Card> pool) {
        List<Card> all = new ArrayList<>(hand);
        all.addAll(pool);
        HandResult best = null;
        // 7选5组合
        List<List<Card>> combinations = combinations(all, 5);
        for (List<Card> comb : combinations) {
            HandResult r = evaluateHand(comb);
            if (best == null || r.compareTo(best) < 0) {
                best = r;
            }
        }
        return best;
    }

    /**
     * 评估5张牌的牌型
     */
    public HandResult evaluateHand(List<Card> cards) {
        // 按点数降序
        cards.sort((a, b) -> b.rank.value - a.rank.value);
        boolean flush = isFlush(cards);
        boolean straight = isStraight(cards);
        Map<Integer, Integer> countMap = new HashMap<>();
        for (Card c : cards)
            countMap.put(c.rank.value, countMap.getOrDefault(c.rank.value, 0) + 1);
        List<Integer> counts = new ArrayList<>(countMap.values());
        counts.sort(Collections.reverseOrder());

        // 皇家同花顺
        if (flush && straight && cards.get(0).rank == Rank.ACE && cards.get(1).rank == Rank.KING) {
            // 皇家同花顺，最大牌一定是A
            return new HandResult(HandType.ROYAL_FLUSH, Arrays.asList(Rank.ACE.value));
        }
        // 同花顺
        if (flush && straight) {
            // 顺子的最大牌
            int maxStraight = getStraightHighCard(cards);
            return new HandResult(HandType.STRAIGHT_FLUSH, Arrays.asList(maxStraight));
        }
        // 四条
        if (counts.get(0) == 4) {
            int four = getKeyByValue(countMap, 4);
            int kicker = getKeyByValue(countMap, 1);
            return new HandResult(HandType.FOUR_OF_A_KIND, Arrays.asList(four, kicker));
        }
        // 葫芦
        if (counts.get(0) == 3 && counts.size() > 1 && counts.get(1) == 2) {
            int three = getKeyByValue(countMap, 3);
            int two = getKeyByValue(countMap, 2);
            return new HandResult(HandType.FULL_HOUSE, Arrays.asList(three, two));
        }
        // 同花
        if (flush) {
            // 同花，按牌点降序
            return new HandResult(HandType.FLUSH, getRanks(cards));
        }
        // 顺子
        if (straight) {
            int maxStraight = getStraightHighCard(cards);
            return new HandResult(HandType.STRAIGHT, Arrays.asList(maxStraight));
        }
        // 三条
        if (counts.get(0) == 3) {
            int three = getKeyByValue(countMap, 3);
            List<Integer> kickers = getKickers(countMap, three, -1);
            List<Integer> ranks = new ArrayList<>();
            ranks.add(three);
            ranks.addAll(kickers);
            return new HandResult(HandType.THREE_OF_A_KIND, ranks);
        }
        // 两对
        if (counts.get(0) == 2 && counts.size() > 1 && counts.get(1) == 2) {
            List<Integer> pairs = getKeysByValue(countMap, 2);
            pairs.sort(Collections.reverseOrder());
            int kicker = getKeyByValue(countMap, 1);
            List<Integer> ranks = new ArrayList<>(pairs);
            ranks.add(kicker);
            return new HandResult(HandType.TWO_PAIR, ranks);
        }
        // 一对
        if (counts.get(0) == 2) {
            int pair = getKeyByValue(countMap, 2);
            List<Integer> kickers = getKickers(countMap, pair, -1);
            List<Integer> ranks = new ArrayList<>();
            ranks.add(pair);
            ranks.addAll(kickers);
            return new HandResult(HandType.ONE_PAIR, ranks);
        }
        // 高牌
        return new HandResult(HandType.HIGH_CARD, getRanks(cards));
    }

    // 获取顺子/同花顺的最大牌点数，支持A2345
    private int getStraightHighCard(List<Card> cards) {
        List<Integer> values = new ArrayList<>();
        for (Card c : cards) values.add(c.rank.value);
        Collections.sort(values, Collections.reverseOrder());
        // A2345特判
        if (values.get(0) == 14 && values.get(1) == 5) {
            return 5;
        }
        return values.get(0);
    }

    private boolean isFlush(List<Card> cards) {
        Suit suit = cards.get(0).suit;
        for (Card c : cards)
            if (c.suit != suit)
                return false;
        return true;
    }

    private boolean isStraight(List<Card> cards) {
        List<Integer> values = new ArrayList<>();
        for (Card c : cards)
            values.add(c.rank.value);
        // 处理A2345顺子
        if (values.get(0) == 14 && values.get(1) == 5) {
            values.set(0, 1);
            Collections.sort(values, Collections.reverseOrder());
        }
        for (int i = 0; i < values.size() - 1; i++) {
            if (values.get(i) - values.get(i + 1) != 1)
                return false;
        }
        return true;
    }

    private List<Integer> getRanks(List<Card> cards) {
        List<Integer> ranks = new ArrayList<>();
        for (Card c : cards)
            ranks.add(c.rank.value);
        return ranks;
    }

    private int getKeyByValue(Map<Integer, Integer> map, int value) {
        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            if (e.getValue() == value)
                return e.getKey();
        }
        return -1;
    }

    private List<Integer> getKeysByValue(Map<Integer, Integer> map, int value) {
        List<Integer> keys = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            if (e.getValue() == value)
                keys.add(e.getKey());
        }
        return keys;
    }

    private List<Integer> getKickers(Map<Integer, Integer> map, int... excludes) {
        Set<Integer> excludeSet = new HashSet<>();
        for (int ex : excludes)
            excludeSet.add(ex);
        List<Integer> kickers = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            if (!excludeSet.contains(e.getKey())) {
                for (int i = 0; i < e.getValue(); i++) {
                    kickers.add(e.getKey());
                }
            }
        }
        kickers.sort(Collections.reverseOrder());
        return kickers;
    }

    // 7选5组合
    private List<List<Card>> combinations(List<Card> cards, int k) {
        List<List<Card>> res = new ArrayList<>();
        combine(cards, k, 0, new ArrayList<>(), res);
        return res;
    }

    private void combine(List<Card> cards, int k, int start, List<Card> path, List<List<Card>> res) {
        if (path.size() == k) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < cards.size(); i++) {
            path.add(cards.get(i));
            combine(cards, k, i + 1, path, res);
            path.remove(path.size() - 1);
        }
    }
}