package com.dnui.poker.service;

import com.dnui.poker.command.*;
import com.dnui.poker.dto.TableStatusVO;
import com.dnui.poker.dto.GameResultVO;
import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.entity.PlayerHand;
import com.dnui.poker.event.GameEvent;
import com.dnui.poker.factory.CardFactory;
import com.dnui.poker.observer.GameObserver;
import com.dnui.poker.observer.WebSocketGameEventListener;
import com.dnui.poker.repository.GameSessionRepository;
import com.dnui.poker.repository.PlayerRepository;
import com.dnui.poker.repository.PublicCardRepository;
import com.dnui.poker.repository.PlayerHandRepository;
import com.dnui.poker.strategy.PokerComparator;
import com.dnui.poker.strategy.PokerCompareStrategy;
import com.dnui.poker.strategy.LongCardCompareStrategy;
import com.dnui.poker.strategy.ShortDeckCompareStrategy;
import com.dnui.poker.template.GameFlowTemplateFactory;
import com.dnui.poker.template.GameFlowTemplate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
    @Getter
    @Autowired
    private DealerService dealerService;
    @Getter
    @Autowired
    private PlayerService playerService;
    @Getter
    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Getter
    @Autowired
    private PlayerRepository playerRepository;
    @Getter
    @Autowired
    private PublicCardRepository publicCardRepository;
    @Autowired
    private PlayerHandRepository playerHandRepository;
    // 修正：PokerComparator 不是 Bean，直接 new
    @Getter
    private final PokerComparator pokerComparator = new PokerComparator();
    @Getter
    @Autowired
    private CommandInvoker commandInvoker;
    // 提供给模板调用：获取事件发布器
    @Getter
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private GameFlowTemplateFactory templateFactory;

    // 提供给模板调用：获取GameObserver
    @Getter
    private final GameObserver gameObserver = new GameObserver();

    public GameService() {
        // 注册监听器（观察者模式）
        gameObserver.addListener(new WebSocketGameEventListener());
    }

    // 提供给模板调用：获取PokerCompareStrategy
    /**
     * -- SETTER --
     * 设置牌型比较策略（可根据房间配置动态注入）
     */
    @Getter
    @Setter
    private PokerCompareStrategy compareStrategy;

    // 工厂+策略模式：初始化不同玩法
    public void initTexasHoldem() {
        CardFactory factory = new CardFactory.StandardDeckFactory(); // 工厂模式
        this.compareStrategy = new LongCardCompareStrategy(); // 策略模式
    }

    public void initShortDeck() {
        CardFactory factory = new CardFactory.ShortDeckFactory(); // 工厂模式
        this.compareStrategy = new ShortDeckCompareStrategy(factory); // 策略模式
    }

    /**
     * 开始新牌局
     * 模板方法模式：流程控制
     * 观察者模式：推送事件
     */
    public void startGame(Long tableId) {
        GameSession session = gameSessionRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));
        session.setActive(true);
        session.setStartTime(new Date());
        gameSessionRepository.save(session);

        // 动态选择模板
        GameFlowTemplate flow = templateFactory.getTemplate(session.getPlayType()); // 工厂+模板方法
        flow.run(session); // 模板方法

        gameObserver.notifyGameStart(tableId); // 观察者
        eventPublisher.publishEvent(new GameEvent(this, tableId, "start")); // 事件
    }

    /**
     * 处理玩家操作
     * 命令模式：封装玩家操作
     */
    public void handlePlayerAction(Long playerId, String action, int amount) {
        // 命令模式
        Command command = switch (action) {
            case "bet" -> new BetCommand(playerId, amount, playerService);
            case "raise" -> new RaiseCommand(playerId, amount, playerService);
            case "call" -> new CallCommand(playerId, playerService);
            case "fold" -> new FoldCommand(playerId, playerService);
            case "check" -> new CheckCommand(playerId, playerService);
            case "allin" -> new AllInCommand(playerId, playerService);
            default -> throw new IllegalArgumentException("未知操作");
        };
        commandInvoker.executeCommand(command); // 命令调用者

        // 模板方法推进流程
        GameSession session = playerService.getPlayerSession(playerId);
        GameFlowTemplate flow = templateFactory.getTemplate(session.getPlayType());
        if (flow instanceof GameFlowTemplate.SupportsStepAdvance advancer) {
            advancer.advance(session);
        }

        // 观察者/事件
        eventPublisher.publishEvent(new GameEvent(this, session.getId(), "action", /* actionDTO */ null));
    }

    /**
     * 结算牌局
     * 模板方法推进到SHOWDOWN
     */
    public void settle(Long tableId) {
        GameSession session = gameSessionRepository.findById(tableId).orElseThrow();
        GameFlowTemplate flow = templateFactory.getTemplate(session.getPlayType());
        // 修正：不能直接调用 protected settle，建议通过流程推进或事件驱动结算
        // 推荐做法：触发流程推进到结算阶段（如有需要可实现一个公共方法或事件）
        if (flow instanceof GameFlowTemplate.SupportsStepAdvance advancer) {
            // 多次推进直到进入SHOWDOWN并自动结算
            while (session.getPhase() != null && session.getPhase().ordinal() < com.dnui.poker.dto.GamePhase.SHOWDOWN.ordinal()) {
                advancer.advance(session);
            }
        }
    }

    // 短牌结算
    public void settleShortDeck(Long tableId) {
        // 这里可以调用 settle(tableId) 或实现短牌专用结算逻辑
        settle(tableId);
    }

    public void dealCard(Long tableId) {
        gameObserver.notifyDealCard(tableId);
        eventPublisher.publishEvent(new GameEvent(this, tableId, "deal"));
    }

    // 构建桌面状态VO
    public TableStatusVO buildTableStatusVO(Long tableId) {
        GameSession session = gameSessionRepository.findById(tableId).orElse(null);
        TableStatusVO vo = new TableStatusVO();
        if (session != null) {
            vo.setTableId(session.getId());
            List<TableStatusVO.PlayerStatusInfo> playerInfos = new ArrayList<>();
            List<Player> players = playerRepository.findByGameSession(session);
            for (Player p : players) {
                TableStatusVO.PlayerStatusInfo info = new TableStatusVO.PlayerStatusInfo();
                info.setPlayerId(p.getId());
                info.setNickname(p.getNickname());
                info.setChips(p.getChips());
                info.setSeatNumber(p.getSeatNumber());
                info.setOnline(p.isOnline());
                info.setBetChips(p.getBetChips());
                info.setStatus(p.getStatus() != null ? p.getStatus().name() : null);
                info.setAvatar(p.getAvatar());
                // 关键：查手牌
                List<String> hand = playerHandRepository.findByPlayerAndGameSession(p, session)
                    .stream()
                    .sorted(Comparator.comparingInt(PlayerHand::getCardOrder))
                    .map(PlayerHand::getCardValue)
                    .toList();
                info.setHand(hand);
                playerInfos.add(info);
            }
            vo.setPlayers(playerInfos);
            // 公共牌
            List<String> publicCards = new ArrayList<>();
            publicCardRepository.findByGameSession(session).forEach(card -> publicCards.add(card.getCardValue()));
            vo.setPublicCards(publicCards);
            vo.setPot(session.getPot());
            vo.setPhase(session.getPhase() != null ? session.getPhase().name() : null);
        }
        return vo;
    }

    // 构建结算VO
    public GameResultVO buildGameResultVO(Long tableId) {
        GameSession session = gameSessionRepository.findById(tableId).orElse(null);
        GameResultVO vo = new GameResultVO();
        if (session != null) {
            // 获胜者信息...（此处略，按你业务逻辑补全 winners 列表）
            // vo.setWinners(...);

            List<GameResultVO.PlayerSettleInfo> allPlayers = new ArrayList<>();
            List<Player> players = playerRepository.findByGameSession(session);
            for (Player p : players) {
                GameResultVO.PlayerSettleInfo info = new GameResultVO.PlayerSettleInfo();
                info.setPlayerId(p.getId());
                info.setNickname(p.getNickname());
                info.setChipsAfter(p.getChips());
                info.setTotalBet(p.getTotalBetChips());
//                // 新增字段补全
//                info.setWinAmount(p.getWinAmount() != null ? p.getWinAmount() : 0); // 需保证Player有此字段
                info.setStatus(p.getStatus() != null ? p.getStatus().name() : null);
                // 查手牌
                List<String> hand = playerHandRepository.findByPlayerAndGameSession(p, session)
                        .stream()
                        .sorted(Comparator.comparingInt(PlayerHand::getCardOrder))
                        .map(PlayerHand::getCardValue)
                        .toList();
                info.setHand(hand);

                allPlayers.add(info);
            }
            vo.setAllPlayers(allPlayers);
            // 公共牌
            List<String> publicCards = new ArrayList<>();
            publicCardRepository.findByGameSession(session).forEach(card -> publicCards.add(card.getCardValue()));
            vo.setPublicCards(publicCards);
            vo.setTotalPot(session.getPot());
//            // 其他字段补全
//            vo.setHandType(session.getHandType()); // 需保证GameSession有此字段
            vo.setShowdown(session.getPhase() != null && session.getPhase().name().equals("SHOWDOWN"));
        }
        return vo;
    }
}