package com.dnui.poker.dto;

import lombok.Data;

/**
 * PlayerDTO
 * 玩家数据传输对象（DTO）
 * 用于前后端数据交互，符合SSM分层思想
 * @author XyeHyin
 * @since 2025/4/24
 */
@Data
public class PlayerDTO {
    /** 玩家ID */
    private Long id;
    /** 昵称 */
    private String nickname;
    /** 剩余筹码 */
    private int chips;
    /** 座位号 */
    private int seatNumber;
    /** 是否在线 */
    private boolean online;
}
