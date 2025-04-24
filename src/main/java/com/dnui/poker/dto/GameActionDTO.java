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
    private String action; // "bet", "fold", "check" 等
    private int amount;    // 下注金额，非下注操作可为0
}
