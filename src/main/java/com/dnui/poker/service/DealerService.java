package com.dnui.poker.service;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.factory.CardFactory;
import com.dnui.poker.factory.CardFactorySelector;
import com.dnui.poker.repository.GameSessionRepository;
import com.dnui.poker.repository.PlayerRepository;
import com.dnui.poker.strategy.PokerComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:44
 * @packageName:IntelliJ IDEA
 * @Description: 荷官服务，负责洗牌、发牌等操作
 * @Version: 1.0
 */
@Service
public class DealerService {

    private List<PokerComparator.Card> deck;

    @Autowired
    private CardFactorySelector cardFactorySelector;

    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private PlayerRepository playerRepository;

    /**
     * 洗牌
     */
    public void shuffle() {
        CardFactory cardFactory = cardFactorySelector.selectFactory();
        deck = cardFactory.createDeck();
        Collections.shuffle(deck);
    }

    /**
     * 发牌
     * @param tableId 桌子/牌局ID
     */
    public void dealCards(Long tableId) {
        GameSession session = gameSessionRepository.findById(tableId).orElseThrow();
        List<Player> players = session.getPlayers();
        if (deck.size() < players.size() * 2) {
            throw new IllegalStateException("牌数不足，无法发牌");
        }
        // 每人发两张手牌
        for (Player player : players) {
            List<PokerComparator.Card> hand = new ArrayList<>();
            hand.add(deck.remove(0));
            hand.add(deck.remove(0));
            player.setHand(hand);
            playerRepository.save(player);
        }
        // 发公共牌（预留，实际流程应分阶段发）
        List<PokerComparator.Card> publicCards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            publicCards.add(deck.remove(0));
        }
        session.setPublicCards(publicCards);
        gameSessionRepository.save(session);
    }

    /**
     * 获取当前洗好的牌堆
     */
    public List<PokerComparator.Card> getDeck() {
        return Collections.unmodifiableList(deck);
    }
}