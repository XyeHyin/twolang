package com.dnui.poker.dto;

import lombok.Data;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 19:02
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class GameActionDTO {
    private Long playerId;
    private String action;
    private int amount;
    private String time; // 操作时间
    private int round;   // 轮次
}
