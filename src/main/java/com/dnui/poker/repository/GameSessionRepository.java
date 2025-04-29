package com.dnui.poker.repository;

import com.dnui.poker.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

/**
 * GameSessionRepository
 * 牌局/房间数据访问层（DAO）
 * 使用Spring Data JPA进行ORM操作，符合SSM风格
 * @author XyeHyin
 * @since 2025/4/24
 */
@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    /**
     * 查询指定时间之后创建的牌局
     * @param startTime 起始时间
     * @return 牌局列表
     */
    List<GameSession> findByStartTimeAfter(Date startTime);

    /**
     * 查询指定时间之前创建的牌局
     * @param startTime 截止时间
     * @return 牌局列表
     */
    List<GameSession> findByStartTimeBefore(Date startTime);

    /**
     * 查询指定时间区间的牌局
     * @param start 起始时间
     * @param end 截止时间
     * @return 牌局列表
     */
    List<GameSession> findByStartTimeBetween(Date start, Date end);

    /**
     * 查询最新的一局
     * @return 最新GameSession
     */
    GameSession findTopByOrderByStartTimeDesc();
}
