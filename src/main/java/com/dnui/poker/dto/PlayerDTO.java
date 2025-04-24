package com.dnui.poker.dto;

import lombok.Data;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 19:01
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class PlayerDTO {
    private Long id;
    private String nickname;
    private int chips;
    private int seatNumber;
    private boolean online;
}
