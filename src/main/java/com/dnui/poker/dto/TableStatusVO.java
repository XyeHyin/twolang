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
    private List<String> publicCards;
    private int pot;
    private String phase;

    @Data
    public static class PlayerStatusInfo {
        private Long playerId;
        private String nickname;
        private int chips;
        private int seatNumber;
        private boolean online;
        private int betChips;
        private String status;
        private String avatar;
        private boolean isCurrent; // 是否当前操作玩家
        private List<String> hand; // 可选
    }
}
