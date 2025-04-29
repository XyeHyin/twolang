package com.dnui.poker.service;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.entity.PlayerHand;
import com.dnui.poker.entity.PublicCard;
import com.dnui.poker.factory.CardFactory;
import com.dnui.poker.factory.CardFactorySelector;
import com.dnui.poker.repository.GameSessionRepository;
import com.dnui.poker.repository.PlayerHandRepository;
import com.dnui.poker.repository.PlayerRepository;
import com.dnui.poker.repository.PublicCardRepository;
import com.dnui.poker.strategy.PokerComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:44
 * @packageName:IntelliJ IDEA
 * @Description: 荷官服务，负责洗牌、发牌等操作
 * @Version: 1.0
 */
@Service
public class DealerService {

    private List<PokerComparator.Card> deck = new ArrayList<>();

    @Autowired
    private CardFactorySelector cardFactorySelector; // 工厂模式

    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerHandRepository playerHandRepository;
    @Autowired
    private PublicCardRepository publicCardRepository;
    @Autowired
    private PlayerService playerService; // 修复：注入PlayerService

    /**
     * 洗牌
     * 工厂模式：通过工厂选择不同牌堆
     */
    public void shuffle() {
        CardFactory cardFactory = cardFactorySelector.selectFactory(); // 工厂模式
        deck = cardFactory.createDeck();
        Collections.shuffle(deck);
    }

    /**
     * 洗短牌
     * 工厂模式
     */
    public void shuffleShortDeck(GameSession session) {
        CardFactory cardFactory = cardFactorySelector.selectShortDeckFactory(); // 工厂模式
        deck = cardFactory.createDeck();
        Collections.shuffle(deck);
    }

    /**
     * 发手牌
     *
     * @param tableId 桌子/牌局ID
     */
    public void dealCards(Long tableId) {
        GameSession session = gameSessionRepository.findById(tableId).orElseThrow();
        dealToPlayers(session); // 复用逻辑
    }

    /**
     * 发手牌到玩家（模板调用）
     */
    public void dealToPlayers(GameSession session) {
        List<Player> players = session.getPlayers();
        if (deck.size() < players.size() * 2) {
            throw new IllegalStateException("牌数不足，无法发牌");
        }
        for (Player player : players) {
            List<PokerComparator.Card> hand = new ArrayList<>();
            hand.add(deck.removeFirst());
            hand.add(deck.removeFirst());
            // 保存到PlayerHand表
            for (int i = 0; i < hand.size(); i++) {
                PlayerHand playerHand = new PlayerHand();
                playerHand.setPlayer(player);
                playerHand.setGameSession(session);
                playerHand.setCardValue(hand.get(i).toString().toUpperCase());
                playerHand.setCardOrder(i + 1);
                playerHandRepository.save(playerHand);
            }
        }
    }

    /**
     * 发公共牌（翻牌）
     */
    public void dealFlop(GameSession session) {
        List<PublicCard> flop = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PokerComparator.Card card = deck.removeFirst();
            PublicCard publicCard = new PublicCard();
            publicCard.setGameSession(session);
            publicCard.setCardValue(card.toString().toUpperCase());
            publicCard.setCardOrder(i + 1);
            flop.add(publicCardRepository.save(publicCard));
        }
        // 你可以将flop合并到session的publicCards字段
    }

    /**
     * 发公共牌（转牌）
     */
    public void dealTurn(GameSession session) {
        PokerComparator.Card card = deck.removeFirst();
        PublicCard publicCard = new PublicCard();
        publicCard.setGameSession(session);
        publicCard.setCardValue(card.toString().toUpperCase());
        publicCard.setCardOrder(4);
        publicCardRepository.save(publicCard);
    }

    /**
     * 发公共牌（河牌）
     */
    public void dealRiver(GameSession session) {
        PokerComparator.Card card = deck.removeFirst();
        PublicCard publicCard = new PublicCard();
        publicCard.setGameSession(session);
        publicCard.setCardValue(card.toString().toUpperCase());
        publicCard.setCardOrder(5);
        publicCardRepository.save(publicCard);
    }

    /**
     * 发一张公共牌（模板调用）
     */
    public void dealPublicCard(GameSession session) {
        // 修复：直接查库获取当前公共牌数量，避免session未同步问题
        int order = (int) publicCardRepository.countByGameSession(session) + 1;
        PokerComparator.Card card = deck.removeFirst();
        PublicCard publicCard = new PublicCard();
        publicCard.setGameSession(session);
        publicCard.setCardValue(card.toString().toUpperCase());
        publicCard.setCardOrder(order);
        publicCardRepository.save(publicCard);
    }

    /**
     * 获取当前洗好的牌堆
     */
    public List<PokerComparator.Card> getDeck() {
        return Collections.unmodifiableList(deck);
    }

    // 盲注扣除功能：命令模式间接调用（通过PlayerService.bet）
    public void deductBlinds(GameSession session, int smallBlind, int bigBlind) {
        List<Player> players = session.getPlayers();
        // 只对状态为ACTIVE或WAITING的前两位玩家扣除盲注
        List<Player> activePlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.getStatus() == Player.PlayerStatus.ACTIVE || p.getStatus() == Player.PlayerStatus.WAITING) {
                activePlayers.add(p);
            }
        }
        if (activePlayers.size() < 2) return;
        Player sb = activePlayers.get(0);
        Player bb = activePlayers.get(1);
        playerService.bet(sb.getId(), smallBlind); // 命令模式间接调用
        playerService.bet(bb.getId(), bigBlind);
        // 可选：设置玩家状态为已下注
        sb.setStatus(Player.PlayerStatus.ACTIVE);
        bb.setStatus(Player.PlayerStatus.ACTIVE);
    }
}