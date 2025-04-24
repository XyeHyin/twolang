package com.dnui.poker.repository;

import com.dnui.poker.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;
/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:46
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
     // 查询指定时间之后创建的牌局
    List<GameSession> findByStartTimeAfter(Date startTime);

    // 查询指定时间之前创建的牌局
    List<GameSession> findByStartTimeBefore(Date startTime);

    // 查询指定时间区间的牌局
    List<GameSession> findByStartTimeBetween(Date start, Date end);

    // 查询最新的一局
    GameSession findTopByOrderByStartTimeDesc();
}
