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
        playerService.resetPlayers(session);
        // 新增：准备阶段扣除小盲和大盲
        dealerService.deductBlinds(session, 50, 100); // 这里50/100为示例金额，可根据实际配置调整
    }

    @Override
    protected void dealCards(GameSession session) {
        // 发手牌（短牌）
        dealerService.dealToPlayers(session);
    }

    @Override
    protected void bettingRounds(GameSession session) {
        // 下注轮
        commandInvoker.executeBettingRound();
    }

    @Override
    protected void revealPublicCards(GameSession session) {
        // 发公共牌
        dealerService.dealPublicCard(session);
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
        int maxBet = session.getPlayers().stream()
                .filter(p -> p.getStatus() == Player.PlayerStatus.ACTIVE || p.getStatus() == Player.PlayerStatus.ALL_IN)
                .mapToInt(Player::getBetChips)
                .max().orElse(0);

        return session.getPlayers().stream()
                .filter(p -> p.getStatus() == Player.PlayerStatus.ACTIVE)
                .allMatch(p -> p.getBetChips() == maxBet);
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
            default -> {
            }
        }
        session.getPlayers().forEach(p -> p.setBetChips(0));
        gameService.getGameSessionRepository().save(session);
    }
}