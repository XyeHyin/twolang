package com.dnui.poker.entity;

import com.dnui.poker.strategy.PokerComparator;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:46
 * @packageName:IntelliJ IDEA
 * @Description: 玩家实体
 * @Version: 1.0
 */

@Entity
@Data
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private int chips; // 玩家当前筹码

    private int seatNumber; // 座位号

    private boolean online;

    private int betChips; // 玩家当前下注的筹码

    private int totalBetChips; // 玩家总下注的筹码

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @Enumerated(EnumType.STRING)
    private PlayerStatus status; // 玩家状态


    public enum PlayerStatus {
        WAITING, ACTIVE, FOLDED, ALL_IN, SETTLED
    }
}