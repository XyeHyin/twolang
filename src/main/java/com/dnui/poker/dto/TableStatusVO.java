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
public class TableStatusVO {
    private Long tableId;
    private List<PlayerStatusInfo> players;
    private List<String> publicCards; // 例如 ["AS", "KH", "7D"]
    private int pot;
    private String phase; // "PRE_FLOP", "FLOP", "TURN", "RIVER", "SHOWDOWN"

    @Data
    public static class PlayerStatusInfo {
        private Long playerId;
        private String nickname;
        private int chips;
        private int seatNumber;
        private boolean online;
        private int betChips; // 当前轮下注
        private String status; // "ACTIVE"/"FOLDED"/"ALL_IN"
        private List<String> hand; // 可选，摊牌时返回
    }
}
