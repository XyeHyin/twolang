package com.dnui.poker.service;

import com.dnui.poker.command.BetCommand;
import com.dnui.poker.command.Command;
import com.dnui.poker.command.FoldCommand;
import com.dnui.poker.strategy.PokerComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:44
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class GameService {
    @Autowired
    private DealerService dealerService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private PokerComparator pokerComparator;

    public void startGame(Long tableId) {
        // 1. 洗牌、发牌
        dealerService.shuffle();
        dealerService.dealCards(tableId);

        // 2. 通知所有玩家游戏开始（Observer推送）
        // gameObserver.notifyGameStart(...);
    }

    public void handlePlayerAction(Long playerId, String action, int amount) {
        Command command;
        switch (action) {
            case "bet" -> command = new BetCommand(amount);
            case "fold" -> command = new FoldCommand();
            default -> throw new IllegalArgumentException("未知操作");
        }
        command.execute();
        // 判断是否进入下一轮或结算
    }

    public void settle(Long tableId) {
        // 1. 获取所有玩家手牌和公共牌
        // 2. 调用pokerComparator.compare(...)进行比牌
        // 3. 结算筹码，推送结果
    }
}
