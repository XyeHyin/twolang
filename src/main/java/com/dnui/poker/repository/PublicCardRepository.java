package com.dnui.poker.repository;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.PublicCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:47
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Repository
public interface PublicCardRepository extends JpaRepository<PublicCard, Long> {
    List<PublicCard> findByGameSession(GameSession gameSession);

    // 新增：统计某局的公共牌数量
    long countByGameSession(GameSession session);
}