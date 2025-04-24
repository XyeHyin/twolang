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
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private int chips; // 玩家当前筹码

    private int seatNumber; // 座位号

    private boolean isOnline;
}