package com.dnui.poker.service;

import com.dnui.poker.adapter.PokerRuleAdapter;
import com.dnui.poker.command.*;
import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.event.GameEvent;
import com.dnui.poker.factory.CardFactory;
import com.dnui.poker.observer.GameObserver;
import com.dnui.poker.observer.WebSocketGameEventListener;
import com.dnui.poker.repository.GameSessionRepository;
import com.dnui.poker.strategy.PokerComparator;
import com.dnui.poker.strategy.PokerCompareStrategy;
import com.dnui.poker.strategy.LongCardCompareStrategy;
import com.dnui.poker.strategy.ShortDeckCompareStrategy;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private ApplicationEventPublisher eventPublisher;

    private final GameObserver gameObserver = new GameObserver();

    public GameService() {
        // 注册监听器
        gameObserver.addListener(new WebSocketGameEventListener());
    }

    /**
     * -- SETTER --
     *  设置牌型比较策略（可根据房间配置动态注入）
     */
    @Setter
    private PokerCompareStrategy compareStrategy;

    // 初始化标准德扑
    public void initTexasHoldem() {
        CardFactory factory = new CardFactory.StandardDeckFactory();
        this.compareStrategy = new LongCardCompareStrategy();
    }

    // 初始化短牌德扑
    public void initShortDeck() {
        CardFactory factory = new CardFactory.ShortDeckFactory();
        this.compareStrategy = new ShortDeckCompareStrategy(factory);
    }

    /**
     * 开始新牌局
     */
    public void startGame(Long tableId) {
        // 1. 通过 TableManager 获取或创建 GameSession
        GameSession session = TableManager.getInstance().getTable(tableId);
        if (session == null) {
            session = new GameSession();
            session.setId(tableId);
            TableManager.getInstance().addTable(tableId, session);
        }

        // 2. 初始化玩法策略
        this.compareStrategy = new LongCardCompareStrategy(); // 或 ShortDeckCompareStrategy

        // 3. 洗牌、发牌
        dealerService.shuffle();
        dealerService.dealCards(tableId);

        // 4. 初始化流程模板（如德扑/短牌）
        GameFlowTemplate flow = new TexasHoldemGameFlow(this, dealerService, commandInvoker);

        // 5. 进入第一轮下注
        flow.startBettingRound(session);

        // 6. 推送事件
        gameObserver.notifyGameStart(tableId);
        eventPublisher.publishEvent(new GameEvent(this, tableId, "start"));
    }

    /**
     * 处理玩家操作
     */
    public void handlePlayerAction(Long playerId, String action, int amount) {
        // 1. 封装命令模式
        Command command = switch (action) {
            case "bet" -> new BetCommand(playerId, amount, playerService);
            case "raise" -> new RaiseCommand(playerId, amount, playerService);
            case "call" -> new CallCommand(playerId, playerService);
            case "fold" -> new FoldCommand(playerId, playerService);
            case "check" -> new CheckCommand(playerId, playerService);
            case "allin" -> new AllInCommand(playerId, playerService);
            default -> throw new IllegalArgumentException("未知操作");
        };
        commandInvoker.executeCommand(command);

        // 2. 判断下注轮是否结束，推进流程
        GameSession session = playerService.getPlayerSession(playerId);
        if (isBettingRoundOver(session)) {
            advanceGamePhase(session);
        }
        if (session.getPhase() == GamePhase.SHOWDOWN) {
            settle(session.getId());
        }

        // 3. 推送玩家操作事件
        eventPublisher.publishEvent(new GameEvent(this, session.getId(), "action", /* actionDTO */ null));
    }

    // 判断当前下注轮是否结束（所有未弃牌/未全下玩家都已操作且筹码相等）
    private boolean isBettingRoundOver(GameSession session) {
        int maxBet = session.getPlayers().stream()
            .filter(p -> p.getStatus() == Player.PlayerStatus.ACTIVE || p.getStatus() == Player.PlayerStatus.ALL_IN)
            .mapToInt(Player::getBetChips)
            .max().orElse(0);

        return session.getPlayers().stream()
            .filter(p -> p.getStatus() == Player.PlayerStatus.ACTIVE)
            .allMatch(p -> p.getBetChips() == maxBet);
    }

    // 推进游戏阶段
    private void advanceGamePhase(GameSession session) {
        switch (session.getPhase()) {
            case PRE_FLOP -> {
                session.setPhase(GamePhase.FLOP);
                // 发三张公共牌
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
        // 重置每个玩家本轮下注
        session.getPlayers().forEach(p -> p.setBetChips(0));
        // 保存session
        // gameSessionRepository.save(session);
    }

    /**
     * 结算牌局
     */
    public void settle(Long tableId) {
        GameSession session = gameSessionRepository.findById(tableId).orElseThrow();
        List<Player> players = session.getPlayers();
        List<PokerComparator.Card> publicCards = session.getPublicCards();
        eventPublisher.publishEvent(new GameEvent(this, tableId, "settle"));
        // 1. 统计每个玩家总下注
        List<Player> activePlayers = players.stream()
            .filter(p -> p.getStatus() != Player.PlayerStatus.FOLDED)
            .toList();

        // 2. 计算所有下注金额，按从小到大排序，准备分池
        List<Integer> allBets = players.stream()
            .map(Player::getTotalBetChips)
            .filter(bet -> bet > 0)
            .distinct()
            .sorted()
            .toList();

        int from = 0;
        for (int i = 0; i < allBets.size(); i++) {
            int to = allBets.get(i);
            int pool = 0;
            List<Player> poolPlayers = players.stream()
                .filter(p -> p.getTotalBetChips() >= to)
                .toList();

            // 计算本池金额
            for (Player p : poolPlayers) {
                int bet = Math.min(p.getTotalBetChips(), to) - from;
                pool += bet;
            }

            // 参与本池的未弃牌玩家
            List<Player> eligible = poolPlayers.stream()
                .filter(p -> p.getStatus() != Player.PlayerStatus.FOLDED)
                .toList();

            // 比牌，找出本池赢家（可能多人平分）
            PokerComparator.HandResult best = null;
            List<Player> winners = new ArrayList<>();
            for (Player p : eligible) {
                PokerComparator.HandResult result = compareStrategy.evaluateBestHand(p.getHandCards(), publicCards);
                if (best == null || result.compareTo(best) > 0) {
                    best = result;
                    winners.clear();
                    winners.add(p);
                } else if (result.compareTo(best) == 0) {
                    winners.add(p);
                }
            }

            // 平分本池
            int share = pool / winners.size();
            int remain = pool % winners.size();
            for (Player win : winners) {
                win.setChips(win.getChips() + share);
            }
            // 剩余筹码给第一个赢家
            if (remain > 0) {
                winners.get(0).setChips(winners.get(0).getChips() + remain);
            }

            from = to;
        }

        // 5. 更新数据库
        players.forEach(playerRepository::save);
        session.setActive(false);
        gameSessionRepository.save(session);
        gameObserver.notifyGameSettle(tableId);
        eventPublisher.publishEvent(new GameEvent(this, tableId, "settle"));
    }

    public void dealCard(Long tableId) {
        // ...发牌逻辑...
        gameObserver.notifyDealCard(tableId);
        eventPublisher.publishEvent(new GameEvent(this, tableId, "deal"));
    }
}