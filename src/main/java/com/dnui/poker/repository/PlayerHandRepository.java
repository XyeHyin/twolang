package com.dnui.poker.repository;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.entity.PlayerHand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 19:56
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Repository
public interface PlayerHandRepository extends JpaRepository<PlayerHand, Long> {
    List<PlayerHand> findByPlayer(Player player);
    List<PlayerHand> findByGameSession(GameSession gameSession);
    List<PlayerHand> findByPlayerAndGameSession(Player player, GameSession gameSession);
}
