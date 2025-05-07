package com.dnui.poker.template;

import com.dnui.poker.dto.GamePhase;
import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.service.DealerService;
import com.dnui.poker.service.PlayerService;
import com.dnui.poker.service.CommandInvoker;
import com.dnui.poker.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 短牌德州扑克流程实现（继承模板方法骨架）
 */
@Component
public class ShortDeckGameFlow extends GameFlowTemplate implements GameFlowTemplate.SupportsStepAdvance {

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
        // 洗短牌、初始化玩家状态
        dealerService.shuffleShortDeck(session);
        dealerService.clearPublicCards(session);
        playerService.resetPlayers(session);

        session.setPhase(GamePhase.PRE_FLOP); // 初始化阶段
        // 新增：准备阶段扣除小盲和大盲
        dealerService.deductBlinds(session, 50, 100); // 这里50/100为示例金额，可根据实际配置调整
        gameService.getGameSessionRepository().save(session);
    }

    @Override
    protected void dealCards(GameSession session) {
        dealerService.dealToPlayers(session);
        gameService.getGameSessionRepository().save(session);
    }

    @Override
    protected void bettingRounds(GameSession session) {
        // 下注轮由前端驱动，通常这里为空实现
        // 如果你有自动机器人下注，可以在这里调用
        // commandInvoker.executeBettingRound();
    }

    @Override
    protected void revealFlop(GameSession session) {
        dealerService.dealFlop(session);
        session.setPhase(GamePhase.FLOP);
        gameService.getGameSessionRepository().save(session);
    }

    @Override
    protected void revealTurn(GameSession session) {
        dealerService.dealTurn(session);
        session.setPhase(GamePhase.TURN);
        gameService.getGameSessionRepository().save(session);
    }

    @Override
    protected void revealRiver(GameSession session) {
        dealerService.dealRiver(session);
        session.setPhase(GamePhase.RIVER);
        gameService.getGameSessionRepository().save(session);
    }

    @Override
    protected void settle(GameSession session) {
        // 结算（短牌规则）
        gameService.settleShortDeck(session.getId());
        // 通知结算事件
        gameService.getGameObserver().notifyGameSettle(session.getId());
        gameService.getEventPublisher().publishEvent(
                new com.dnui.poker.event.GameEvent(this, session.getId(), "settle")
        );
        session.setPhase(GamePhase.SHOWDOWN);
        gameService.getGameSessionRepository().save(session);
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
        return "SHORT_DECK";
    }

    // 支持逐步推进
    @Override
    public void advance(GameSession session) {
        if (isBettingRoundOver(session)) {
            advanceGamePhase(session);
        }
        long activeCount = session.getPlayers().stream()
                .filter(p -> p.getStatus() == Player.PlayerStatus.ACTIVE || p.getStatus() == Player.PlayerStatus.ALL_IN)
                .count();
        if (activeCount <= 1 || session.getPhase() == GamePhase.SHOWDOWN) {
            settle(session);
        }
    }

    private boolean isBettingRoundOver(GameSession session) {
    // 只考虑未弃牌/未全下的玩家
    List<Player> activePlayers = session.getPlayers().stream()
            .filter(p -> p.getStatus() != Player.PlayerStatus.FOLDED && p.getStatus() != Player.PlayerStatus.ALL_IN)
            .toList();

    // 没有玩家处于 WAITING_FOR_ACTION
    boolean allActed = activePlayers.stream()
            .noneMatch(p -> p.getStatus() == Player.PlayerStatus.WAITING_FOR_ACTION);

    // 所有活跃玩家下注额相等
    int maxBet = session.getPlayers().stream()
            .mapToInt(Player::getBetChips)
            .max().orElse(0);

    boolean allBetEqual = activePlayers.stream()
            .allMatch(p -> p.getBetChips() == maxBet);

    // 至少有两名活跃玩家
    boolean enoughPlayers = activePlayers.size() >= 2;

    return allActed && allBetEqual && enoughPlayers;
}

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
            default -> {}
        }
        // 每轮结束后重置下注
        session.getPlayers().forEach(p -> p.setBetChips(0));
        gameService.getGameSessionRepository().save(session);
    }
}