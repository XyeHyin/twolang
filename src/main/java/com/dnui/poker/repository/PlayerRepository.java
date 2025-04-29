package com.dnui.poker.repository;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PlayerRepository
 * 玩家数据访问层（DAO）
 * 负责玩家相关的数据库操作
 *
 * @author XyeHyin
 * @since 2025/4/24
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    /**
     * 根据昵称查找玩家
     *
     * @param nickname 昵称
     * @return Optional<Player>
     */
    Optional<Player> findByNickname(String nickname);

    /**
     * 查询某个牌局下的所有玩家
     *
     * @param gameSession 牌局
     * @return 玩家列表
     */
    List<Player> findByGameSession(GameSession gameSession);
}