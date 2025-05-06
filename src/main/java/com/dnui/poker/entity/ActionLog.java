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
@Table(name = "action_log")
public class ActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Player player;

    @ManyToOne
    private GameSession gameSession;

    private String actionType; // "bet", "raise", "call", "fold", "check" 等

    private int amount;

    private int round;

    private String ip; // 可选

    private String device; // 可选

    @Temporal(TemporalType.TIMESTAMP)
    private Date actionTime;
}