package com.dnui.poker.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 19:15
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Entity
@Data
@Table (name = "player_hand")
public class PlayerHand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @Column(length = 4)
    private String cardValue; // 统一大写，如 "AS", "KH"

    private int cardOrder; // 1-2
}