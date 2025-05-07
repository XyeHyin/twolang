package com.dnui.poker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:46
 * @packageName: IntelliJ IDEA
 * @Description: 牌局/房间的实体
 * @Version: 1.0
 */
@Entity
@Data
@Table(
    name = "game_session",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"table_name"})
    }
)
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", nullable = false, unique = true, length = 64)
    private String tableName;

    private int maxPlayers;

    private String playType; // "TEXAS", "SHORT_DECK" 等

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players;

    @Enumerated(EnumType.STRING)
    private com.dnui.poker.dto.GamePhase phase; // 牌局阶段

    private boolean isActive; // 是否为当前活跃牌局

    private int pot = 0;

    private int currentRound; // 0-翻牌前, 1-翻牌, 2-转牌, 3-河牌
    
    private Integer currentSeat; // 当前操作玩家座位号
}
