package com.dnui.poker.strategy;

import java.util.*;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 22:43
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
public class LongCardCompareStrategy implements PokerCompareStrategy {
    @Override
    public int compare(List<PokerComparator.Card> handA, List<PokerComparator.Card> handB, List<PokerComparator.Card> pool) {
        PokerComparator.HandResult resultA = evaluateBestHand(handA, pool);
        PokerComparator.HandResult resultB = evaluateBestHand(handB, pool);
        int cmp = resultA.compareTo(resultB);
        if (cmp > 0)  return 1;
        if (cmp < 0)  return -1;
        return 0;
    }

    @Override
    public PokerComparator.HandResult evaluateBestHand(List<PokerComparator.Card> hand, List<PokerComparator.Card> pool) {
        List<PokerComparator.Card> all = new ArrayList<>(hand);
        all.addAll(pool);
        PokerComparator.HandResult best = null;
        List<List<PokerComparator.Card>> combinations = combinations(all, 5);
        for (List<PokerComparator.Card> comb : combinations) {
            PokerComparator.HandResult r = evaluateHand(comb);
            if (best == null || r.compareTo(best) < 0) {
                best = r;
            }
        }
        return best;
    }

    @Override
    public PokerComparator.HandResult evaluateHand(List<PokerComparator.Card> cards) {
        cards.sort((a, b) -> b.rank.value - a.rank.value);
        boolean flush = isFlush(cards);
        boolean straight = isStraight(cards);
        Map<Integer, Integer> countMap = new HashMap<>();
        for (PokerComparator.Card c : cards)
            countMap.put(c.rank.value, countMap.getOrDefault(c.rank.value, 0) + 1);
        List<Integer> counts = new ArrayList<>(countMap.values());
        counts.sort(Collections.reverseOrder());

        if (flush && straight && cards.get(0).rank == PokerComparator.Rank.ACE && cards.get(1).rank == PokerComparator.Rank.KING) {
            return new PokerComparator.HandResult(PokerComparator.HandType.ROYAL_FLUSH, Arrays.asList(PokerComparator.Rank.ACE.value));
        }
        if (flush && straight) {
            int maxStraight = getStraightHighCard(cards);
            return new PokerComparator.HandResult(PokerComparator.HandType.STRAIGHT_FLUSH, Arrays.asList(maxStraight));
        }
        if (counts.get(0) == 4) {
            int four = getKeyByValue(countMap, 4);
            int kicker = getKeyByValue(countMap, 1);
            return new PokerComparator.HandResult(PokerComparator.HandType.FOUR_OF_A_KIND, Arrays.asList(four, kicker));
        }
        if (counts.get(0) == 3 && counts.size() > 1 && counts.get(1) == 2) {
            int three = getKeyByValue(countMap, 3);
            int two = getKeyByValue(countMap, 2);
            return new PokerComparator.HandResult(PokerComparator.HandType.FULL_HOUSE, Arrays.asList(three, two));
        }
        if (flush) {
            return new PokerComparator.HandResult(PokerComparator.HandType.FLUSH, getRanks(cards));
        }
        if (straight) {
            int maxStraight = getStraightHighCard(cards);
            return new PokerComparator.HandResult(PokerComparator.HandType.STRAIGHT, Arrays.asList(maxStraight));
        }
        if (counts.get(0) == 3) {
            int three = getKeyByValue(countMap, 3);
            List<Integer> kickers = getKickers(countMap, three, -1);
            List<Integer> ranks = new ArrayList<>();
            ranks.add(three);
            ranks.addAll(kickers);
            return new PokerComparator.HandResult(PokerComparator.HandType.THREE_OF_A_KIND, ranks);
        }
        if (counts.get(0) == 2 && counts.size() > 1 && counts.get(1) == 2) {
            List<Integer> pairs = getKeysByValue(countMap, 2);
            pairs.sort(Collections.reverseOrder());
            int kicker = getKeyByValue(countMap, 1);
            List<Integer> ranks = new ArrayList<>(pairs);
            ranks.add(kicker);
            return new PokerComparator.HandResult(PokerComparator.HandType.TWO_PAIR, ranks);
        }
        if (counts.get(0) == 2) {
            int pair = getKeyByValue(countMap, 2);
            List<Integer> kickers = getKickers(countMap, pair, -1);
            List<Integer> ranks = new ArrayList<>();
            ranks.add(pair);
            ranks.addAll(kickers);
            return new PokerComparator.HandResult(PokerComparator.HandType.ONE_PAIR, ranks);
        }
        return new PokerComparator.HandResult(PokerComparator.HandType.HIGH_CARD, getRanks(cards));
    }

    // 工具方法（可提取为静态工具类）
    private int getStraightHighCard(List<PokerComparator.Card> cards) {
        List<Integer> values = new ArrayList<>();
        for (PokerComparator.Card c : cards) values.add(c.rank.value);
        Collections.sort(values, Collections.reverseOrder());
        if (values.get(0) == 14 && values.get(1) == 5) {
            return 5;
        }
        return values.get(0);
    }

    private boolean isFlush(List<PokerComparator.Card> cards) {
        PokerComparator.Suit suit = cards.get(0).suit;
        for (PokerComparator.Card c : cards)
            if (c.suit != suit)
                return false;
        return true;
    }

    private boolean isStraight(List<PokerComparator.Card> cards) {
        List<Integer> values = new ArrayList<>();
        for (PokerComparator.Card c : cards)
            values.add(c.rank.value);
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

    private List<Integer> getRanks(List<PokerComparator.Card> cards) {
        List<Integer> ranks = new ArrayList<>();
        for (PokerComparator.Card c : cards)
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

    private List<List<PokerComparator.Card>> combinations(List<PokerComparator.Card> cards, int k) {
        List<List<PokerComparator.Card>> res = new ArrayList<>();
        combine(cards, k, 0, new ArrayList<>(), res);
        return res;
    }

    private void combine(List<PokerComparator.Card> cards, int k, int start, List<PokerComparator.Card> path, List<List<PokerComparator.Card>> res) {
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
