package com.dnui.poker.template;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.service.DealerService;
import com.dnui.poker.service.PlayerService;
import com.dnui.poker.service.CommandInvoker;
import com.dnui.poker.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 德州扑克标准流程实现（继承模板方法骨架）
 */
@Component
public class TexasHoldemGameFlow extends GameFlowTemplate {

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
        // 洗牌、初始化玩家状态
        dealerService.shuffle(session);
        playerService.resetPlayers(session);
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
    }

    @Override
    protected void finish(GameSession session) {
        // 清理，准备下局
        playerService.finishRound(session);
    }
}