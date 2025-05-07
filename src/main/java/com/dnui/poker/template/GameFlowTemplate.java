package com.dnui.poker.template;

import com.dnui.poker.entity.GameSession;

public abstract class GameFlowTemplate {

    // 只允许子类和本包调用
    protected abstract void prepare(GameSession session);
    protected abstract void dealCards(GameSession session);
    protected abstract void bettingRounds(GameSession session);
    protected abstract void revealFlop(GameSession session);
    protected abstract void revealTurn(GameSession session);
    protected abstract void revealRiver(GameSession session);
    protected abstract void settle(GameSession session);
    protected abstract void finish(GameSession session);
    public abstract String getPlayType();

    // 阶段推进方法，外部只能调用这些
    public final void runPreflop(GameSession session) {
        prepare(session);
        dealCards(session);
        bettingRounds(session);
    }

    public final void runFlop(GameSession session) {
        revealFlop(session);
        bettingRounds(session);
    }

    public final void runTurn(GameSession session) {
        revealTurn(session);
        bettingRounds(session);
    }

    public final void runRiver(GameSession session) {
        revealRiver(session);
        bettingRounds(session);
    }

    public final void runShowdown(GameSession session) {
        settle(session);
        finish(session);
    }

    // 支持逐步推进流程的标记接口
    public interface SupportsStepAdvance {
        void advance(GameSession session);
    }
}
