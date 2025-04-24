package com.dnui.poker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 19:17
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Entity
@Data
public class ActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private String actionType; // "bet", "fold", "check"等

    private int amount;

    private Date actionTime;
}
