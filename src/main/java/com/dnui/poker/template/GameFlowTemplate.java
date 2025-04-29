package com.dnui.poker.template;

import com.dnui.poker.entity.GameSession;

/**
 * 德州扑克牌局流程模板方法模式骨架
 * 子类只需实现各步骤细节即可
 */
public abstract class GameFlowTemplate {

    // 1. 牌局准备（如洗牌、初始化玩家状态等）
    protected abstract void prepare(GameSession session);

    // 2. 发牌
    protected abstract void dealCards(GameSession session);

    // 3. 下注轮（可多轮）
    protected abstract void bettingRounds(GameSession session);

    // 4. 翻牌/转牌/河牌（公共牌发放）
    protected abstract void revealPublicCards(GameSession session);

    // 5. 结算
    protected abstract void settle(GameSession session);

    // 6. 结束清理
    protected abstract void finish(GameSession session);

    // 返回该模板支持的玩法类型（如 "TEXAS", "SHORT_DECK"）
    public abstract String getPlayType();

    // 模板方法：控制整个牌局流程
    public final void run(GameSession session) {
        prepare(session);// 准备阶段
        dealCards(session);// 发牌阶段
        bettingRounds(session);// 下注阶段
        revealPublicCards(session);// 翻牌阶段
        bettingRounds(session); // 翻牌后下注
        revealPublicCards(session);// 转牌阶段
        bettingRounds(session); // 转牌后下注
        revealPublicCards(session);// 河牌阶段
        bettingRounds(session); // 河牌后下注
        settle(session);// 结算阶段
        finish(session);// 清理阶段
    }

    // 可选：支持流程推进的模板实现该接口
    public interface SupportsStepAdvance {
        void advance(GameSession session);
    }
}
