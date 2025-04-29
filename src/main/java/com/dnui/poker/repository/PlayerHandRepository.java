package com.dnui.poker.repository;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.entity.PlayerHand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PlayerHandRepository
 * 玩家手牌数据访问层（DAO）
 * 负责玩家手牌相关的数据库操作
 *
 * @author XyeHyin
 * @since 2025/4/24
 */
@Repository
public interface PlayerHandRepository extends JpaRepository<PlayerHand, Long> {

    /**
     * 查询某玩家的所有手牌
     *
     * @param player 玩家
     * @return 手牌列表
     */
    List<PlayerHand> findByPlayer(Player player);

    /**
     * 查询某牌局的所有手牌
     *
     * @param gameSession 牌局
     * @return 手牌列表
     */
    List<PlayerHand> findByGameSession(GameSession gameSession);

    /**
     * 查询某玩家在某牌局的手牌
     *
     * @param player      玩家
     * @param gameSession 牌局
     * @return 手牌列表
     */
    List<PlayerHand> findByPlayerAndGameSession(Player player, GameSession gameSession);
}
