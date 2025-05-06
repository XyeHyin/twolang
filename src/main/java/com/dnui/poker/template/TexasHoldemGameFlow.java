package com.dnui.poker.template;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.service.DealerService;
import com.dnui.poker.service.PlayerService;
import com.dnui.poker.service.CommandInvoker;
import com.dnui.poker.service.GameService;
import com.dnui.poker.dto.GamePhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 德州扑克标准流程实现（继承模板方法骨架）
 */
@Component
public class TexasHoldemGameFlow extends GameFlowTemplate implements GameFlowTemplate.SupportsStepAdvance {

    @Autowired
    private DealerService dealerService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private CommandInvoker commandInvoker;
    @Autowired
    @Lazy
    private GameService gameService;

    @Override
    protected void prepare(GameSession session) {
        // 洗牌、初始化玩家状态
        dealerService.shuffle();
        playerService.resetPlayers(session);
        // 新增：准备阶段扣除小盲和大盲
        dealerService.deductBlinds(session, 50, 100); // 这里50/100为示例金额，可根据实际配置调整
    }

    @Override
    protected void dealCards(GameSession session) {
        // 发手牌
        dealerService.dealToPlayers(session);
    }

    @Override
    protected void bettingRounds(GameSession session) {
        // 下注轮（可根据session阶段判断是前注、翻牌后、转牌后、河牌后）
        commandInvoker.executeBettingRound();
    }

    @Override
    protected void revealPublicCards(GameSession session) {
        // 发公共牌（翻牌/转牌/河牌）
        dealerService.dealPublicCard(session);
    }

    @Override
    protected void settle(GameSession session) {
        // 结算
        gameService.settle(session.getId());
        // 通知结算事件
        gameService.getGameObserver().notifyGameSettle(session.getId());
        gameService.getEventPublisher().publishEvent(
                new com.dnui.poker.event.GameEvent(this, session.getId(), "settle")
        );
    }

    @Override
    protected void finish(GameSession session) {
        // 清理，准备下局
        playerService.finishRound(session);
        // 通知结束事件
        gameService.getGameObserver().notifyGameEnd(session.getId());
        gameService.getEventPublisher().publishEvent(
                new com.dnui.poker.event.GameEvent(this, session.getId(), "finish")
        );
    }

    @Override
    public String getPlayType() {
        return "TEXAS";
    }

    // 新增：流程推进
    @Override
    public void advance(GameSession session) {
        // 判断下注轮是否结束，推进阶段
        if (isBettingRoundOver(session)) {
            advanceGamePhase(session);
        }
        // 新增：如果只剩一名未弃牌玩家，直接结算
        long activeCount = session.getPlayers().stream()
                .filter(p -> p.getStatus() == Player.PlayerStatus.ACTIVE || p.getStatus() == Player.PlayerStatus.ALL_IN)
                .count();
        if (activeCount <= 1 || session.getPhase() == GamePhase.SHOWDOWN) {
            settle(session);
        }
    }

    // 判断下注轮是否结束（与原GameService逻辑一致）
    private boolean isBettingRoundOver(GameSession session) {
        int maxBet = session.getPlayers().stream()
                .filter(p -> p.getStatus() == Player.PlayerStatus.ACTIVE || p.getStatus() == Player.PlayerStatus.ALL_IN)
                .mapToInt(Player::getBetChips)
                .max().orElse(0);

        return session.getPlayers().stream()
                .filter(p -> p.getStatus() == Player.PlayerStatus.ACTIVE)
                .allMatch(p -> p.getBetChips() == maxBet);
    }

    // 推进游戏阶段（与原GameService逻辑一致）
    private void advanceGamePhase(GameSession session) {
        switch (session.getPhase()) {
            case PRE_FLOP -> {
                session.setPhase(GamePhase.FLOP);
                dealerService.dealFlop(session);
            }
            case FLOP -> {
                session.setPhase(GamePhase.TURN);
                dealerService.dealTurn(session);
            }
            case TURN -> {
                session.setPhase(GamePhase.RIVER);
                dealerService.dealRiver(session);
            }
            case RIVER -> session.setPhase(GamePhase.SHOWDOWN);
            default -> {
            }
        }
        // 重置每个玩家本轮下注
        session.getPlayers().forEach(p -> p.setBetChips(0));
        // 保存session
        gameService.getGameSessionRepository().save(session);
    }
}