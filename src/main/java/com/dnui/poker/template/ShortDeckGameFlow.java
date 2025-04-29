package com.dnui.poker.template;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.service.DealerService;
import com.dnui.poker.service.PlayerService;
import com.dnui.poker.service.CommandInvoker;
import com.dnui.poker.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 短牌德州扑克流程实现（继承模板方法骨架）
 */
@Component
public class ShortDeckGameFlow extends GameFlowTemplate {

    @Autowired
    private DealerService dealerService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private CommandInvoker commandInvoker;
    @Autowired
    private GameService gameService;

    @Override
    protected void prepare(GameSession session) {
        // 洗短牌、初始化玩家状态
        dealerService.shuffleShortDeck(session);
        playerService.resetPlayers(session);
    }

    @Override
    protected void dealCards(GameSession session) {
        // 发手牌（短牌）
        dealerService.dealToPlayers(session);
    }

    @Override
    protected void bettingRounds(GameSession session) {
        // 下注轮
        commandInvoker.executeBettingRound(session);
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
    }

    @Override
    protected void finish(GameSession session) {
        // 清理，准备下局
        playerService.finishRound(session);
    }
}