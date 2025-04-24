package com.dnui.poker.repository;

import com.dnui.poker.entity.ActionLog;
import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 19:57
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    List<ActionLog> findByGameSession(GameSession gameSession);
    List<ActionLog> findByPlayer(Player player);
    List<ActionLog> findByGameSessionAndPlayer(GameSession gameSession, Player player);
}
