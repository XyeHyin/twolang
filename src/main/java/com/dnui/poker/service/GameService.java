package com.dnui.poker.service;

import com.dnui.poker.command.*;
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
    @Autowired
    private CommandInvoker commandInvoker;

    public void startGame(Long tableId) {
        dealerService.shuffle();
        dealerService.dealCards(tableId);
        // Observer推送游戏开始
    }

    public void handlePlayerAction(Long playerId, String action, int amount) {
        Command command;
        switch (action) {
            case "bet" -> command = new BetCommand(amount);
            case "fold" -> command = new FoldCommand();
            case "check" -> command = new CheckCommand();
            default -> throw new IllegalArgumentException("未知操作");
        }
        commandInvoker.executeCommand(command);
        // 判断是否进入下一轮或结算
    }

    public void settle(Long tableId) {
        // 获取玩家手牌和公共牌
        // 调用pokerComparator.compare(...)
        // 结算筹码，推送结果
    }
}
