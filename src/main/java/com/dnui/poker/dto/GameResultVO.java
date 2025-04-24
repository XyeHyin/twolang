package com.dnui.poker.dto;

import lombok.Data;
import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 19:03
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class GameResultVO {
    private Long winnerId;
    private List<Long> drawPlayerIds; // 平局玩家ID
    private String winHandType; // "STRAIGHT_FLUSH"等
    private List<String> winCards; // ["AS", "KS", "QS", "JS", "10S"]
    private int winPot;
}
