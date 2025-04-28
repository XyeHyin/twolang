package com.dnui.poker.strategy;

import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 22:42
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
public interface PokerCompareStrategy {
    int compare(List<PokerComparator.Card> handA, List<PokerComparator.Card> handB, List<PokerComparator.Card> pool);

    /**
     * 评估5张牌的牌型
     */
    PokerComparator.HandResult evaluateHand(List<PokerComparator.Card> cards);

    /**
     * 计算玩家7张牌（2手牌+5公牌）中最大的5张组合
     */
    PokerComparator.HandResult evaluateBestHand(List<PokerComparator.Card> hand, List<PokerComparator.Card> pool);
}
