package com.dnui.poker.repository;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:46
 * @packageName:IntelliJ IDEA
 * @Description: 玩家数据访问层
 * @Version: 1.0
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByNickname(String nickname);
    List<Player> findByGameSession(GameSession gameSession);
}