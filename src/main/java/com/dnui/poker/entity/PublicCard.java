package com.dnui.poker.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:46
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Entity
@Data
@Table (name = "public_card")
public class PublicCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @Column(length = 8)
    private String cardValue;

    private int cardOrder; // 1-5
}