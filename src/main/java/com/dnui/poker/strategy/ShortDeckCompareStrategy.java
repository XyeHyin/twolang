package com.dnui.poker.strategy;

import com.dnui.poker.factory.CardFactory;

import java.util.*;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 22:44
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
public class ShortDeckCompareStrategy implements PokerCompareStrategy {
    private final CardFactory cardFactory;

    public ShortDeckCompareStrategy(CardFactory cardFactory) {
        this.cardFactory = cardFactory;
    }

    public List<PokerComparator.Card> getShortDeck() {
        // 推荐直接用工厂获取
        return cardFactory.createDeck();
    }

    @Override
    public int compare(List<PokerComparator.Card> handA, List<PokerComparator.Card> handB, List<PokerComparator.Card> pool) {
        PokerComparator.HandResult resultA = evaluateBestHand(handA, pool);
        PokerComparator.HandResult resultB = evaluateBestHand(handB, pool);
        int typeA = adjustHandType(resultA.type);
        int typeB = adjustHandType(resultB.type);
        if (typeA != typeB) return Integer.compare(typeA, typeB);
        return resultA.compareTo(resultB);
    }

    @Override
    public PokerComparator.HandResult evaluateBestHand(List<PokerComparator.Card> hand, List<PokerComparator.Card> pool) {
        List<PokerComparator.Card> all = new ArrayList<>(hand);
        all.addAll(pool);
        PokerComparator.HandResult best = null;
        for (List<PokerComparator.Card> comb : combinations(all, 5)) {
            PokerComparator.HandResult r = evaluateHand(comb);
            if (best == null || compareHandResult(r, best) > 0) {
                best = r;
            }
        }
        return best;
    }

    @Override
    public PokerComparator.HandResult evaluateHand(List<PokerComparator.Card> cards) {
        cards.sort((a, b) -> b.rank.value - a.rank.value);
        boolean flush = isFlush(cards);
        boolean straight = isShortDeckStraight(cards);
        Map<Integer, Integer> countMap = new HashMap<>();
        for (PokerComparator.Card c : cards)
            countMap.put(c.rank.value, countMap.getOrDefault(c.rank.value, 0) + 1);
        List<Integer> counts = new ArrayList<>(countMap.values());
        counts.sort(Collections.reverseOrder());

        if (flush && straight && isRoyal(cards)) {
            return new PokerComparator.HandResult(PokerComparator.HandType.ROYAL_FLUSH, List.of(PokerComparator.Rank.ACE.value));
        }
        if (flush && straight) {
            int maxStraight = getStraightHighCard(cards);
            return new PokerComparator.HandResult(PokerComparator.HandType.STRAIGHT_FLUSH, List.of(maxStraight));
        }
        if (counts.get(0) == 4) {
            int four = getKeyByValue(countMap, 4);
            int kicker = getKickers(countMap, four).get(0);
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
            return new PokerComparator.HandResult(PokerComparator.HandType.STRAIGHT, List.of(maxStraight));
        }
        if (counts.get(0) == 3) {
            int three = getKeyByValue(countMap, 3);
            List<Integer> kickers = getKickers(countMap, three);
            List<Integer> ranks = new ArrayList<>();
            ranks.add(three);
            ranks.addAll(kickers);
            return new PokerComparator.HandResult(PokerComparator.HandType.THREE_OF_A_KIND, ranks);
        }
        if (counts.get(0) == 2 && counts.size() > 1 && counts.get(1) == 2) {
            List<Integer> pairs = getKeysByValue(countMap, 2);
            pairs.sort(Collections.reverseOrder());
            List<Integer> kickers = getKickers(countMap, pairs.get(0), pairs.get(1));
            int kicker = kickers.isEmpty() ? -1 : kickers.get(0);
            List<Integer> ranks = new ArrayList<>(pairs);
            ranks.add(kicker);
            return new PokerComparator.HandResult(PokerComparator.HandType.TWO_PAIR, ranks);
        }
        if (counts.get(0) == 2) {
            int pair = getKeyByValue(countMap, 2);
            List<Integer> kickers = getKickers(countMap, pair);
            List<Integer> ranks = new ArrayList<>();
            ranks.add(pair);
            ranks.addAll(kickers);
            return new PokerComparator.HandResult(PokerComparator.HandType.ONE_PAIR, ranks);
        }
        return new PokerComparator.HandResult(PokerComparator.HandType.HIGH_CARD, getRanks(cards));
    }

    // 工具方法
    private boolean isFlush(List<PokerComparator.Card> cards) {
        PokerComparator.Suit suit = cards.get(0).suit;
        for (PokerComparator.Card c : cards)
            if (c.suit != suit)
                return false;
        return true;
    }

    // 短牌顺子判定（A6789为最大顺子，A可作5顺子的1，也可作9顺子的14）
    private boolean isShortDeckStraight(List<PokerComparator.Card> cards) {
        List<Integer> values = new ArrayList<>();
        for (PokerComparator.Card c : cards)
            values.add(c.rank.value);
        Set<Integer> unique = new HashSet<>(values);
        if (unique.size() != 5) return false;
        Collections.sort(values, Collections.reverseOrder());
        // 短牌A6789顺子
        if (values.equals(Arrays.asList(14, 9, 8, 7, 6))) {
            return true;
        }
        // 标准顺子（含A-10-9-8-7等）
        for (int i = 0; i < values.size() - 1; i++) {
            if (values.get(i) - values.get(i + 1) != 1)
                return false;
        }
        return true;
    }

    private boolean isRoyal(List<PokerComparator.Card> cards) {
        Set<Integer> royal = new HashSet<>(Arrays.asList(14, 13, 12, 11, 10));
        for (PokerComparator.Card c : cards)
            if (!royal.contains(c.rank.value))
                return false;
        return true;
    }

    private int getStraightHighCard(List<PokerComparator.Card> cards) {
        List<Integer> values = new ArrayList<>();
        for (PokerComparator.Card c : cards) values.add(c.rank.value);
        Collections.sort(values, Collections.reverseOrder());
        if (values.equals(Arrays.asList(14, 9, 8, 7, 6))) {
            return 9; // 短牌A6789顺子最大为9
        }
        return values.get(0);
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

    private List<Integer> getRanks(List<PokerComparator.Card> cards) {
        List<Integer> ranks = new ArrayList<>();
        for (PokerComparator.Card c : cards)
            ranks.add(c.rank.value);
        ranks.sort(Collections.reverseOrder());
        return ranks;
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

    // 葫芦大于同花（短牌规则）
    private int adjustHandType(PokerComparator.HandType type) {
        if (type == PokerComparator.HandType.FLUSH) return 3; // 比葫芦低
        if (type == PokerComparator.HandType.FULL_HOUSE) return 4;
        return type.ordinal();
    }

    // 比较两个HandResult，按短牌规则
    private int compareHandResult(PokerComparator.HandResult a, PokerComparator.HandResult b) {
        int typeA = adjustHandType(a.type);
        int typeB = adjustHandType(b.type);
        if (typeA != typeB) return Integer.compare(typeA, typeB);
        return a.compareTo(b);
    }

    // 可供模板调用的短牌结算方法
    public void settleShortDeck(Long tableId) {
        // 这里应调用GameService的短牌结算逻辑
        // 具体实现略
    }
}
