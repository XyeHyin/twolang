package com.dnui.poker.service;

import com.dnui.poker.adapter.PokerRuleAdapter;
import com.dnui.poker.command.*;
import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.repository.GameSessionRepository;
import com.dnui.poker.strategy.PokerComparator;
import com.dnui.poker.strategy.PokerCompareStrategy;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:44
 * @packageName:IntelliJ IDEA
 * @Description: 牌局服务，负责流程控制和结算
 * @Version: 1.0
 */
@Service
public class GameService {
    @Autowired
    private DealerService dealerService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private PokerComparator pokerComparator;
    @Autowired
    private CommandInvoker commandInvoker;
    @Autowired
    private PokerRuleAdapter ruleAdapter;

    /**
     * -- SETTER --
     *  设置牌型比较策略（可根据房间配置动态注入）
     */
    @Setter
    private PokerCompareStrategy compareStrategy;

    /**
     * 开始新牌局
     */
    public void startGame(Long tableId) {
        dealerService.shuffle();
        dealerService.dealCards(tableId);
        // TODO: Observer推送游戏开始事件
    }

    /**
     * 处理玩家操作
     */
    public void handlePlayerAction(Long playerId, String action, int amount) {
        Command command;
        switch (action) {
            case "bet" -> command = new BetCommand(playerId, amount, playerService);
            case "fold" -> command = new FoldCommand(playerId, playerService);
            case "check" -> command = new CheckCommand(playerId, playerService);
            default -> throw new IllegalArgumentException("未知操作");
        }
        commandInvoker.executeCommand(command);
        // TODO: 判断是否进入下一轮或结算
    }

    /**
     * 结算牌局
     */
    public void settle(Long tableId) {
        GameSession session = gameSessionRepository.findById(tableId).orElseThrow();
        List<Player> players = session.getPlayers();
        List<String> publicCards = session.getPublicCards();
        // TODO: 获取每个玩家手牌，调用compareStrategy进行比较
        // int result = compareStrategy.compare(handA, handB, publicCards);
        // TODO: 结算筹码，推送结果
    }

    /**
     * 判断是否为顺子
     */
    public boolean isStraight(List<PokerComparator.Card> cards) {
        return ruleAdapter.isStraight(cards);
    }

}