package com.dnui.poker.repository;

import com.dnui.poker.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:46
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUsername(String username);
    List<Player> findByGameSessionId(Long gameSessionId);
}