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
    private Player player;

    @ManyToOne
    private GameSession gameSession;

    private String actionType; // "bet", "raise", "call", "fold", "check" 等

    private int amount; // 操作金额，非下注类为0

    private int round; // 第几轮（翻牌前/翻牌/转牌/河牌）

    private Date actionTime;
}
