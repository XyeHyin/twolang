package com.dnui.poker.dto;

import lombok.Data;
import java.util.List;
@Data
public class GameResultVO {
    private List<WinnerInfo> winners;
    private List<PlayerSettleInfo> allPlayers;
    private List<String> publicCards;
    private int totalPot;
    private String handType;
    private boolean isShowdown;

    @Data
    public static class WinnerInfo {
        private Long playerId;
        private String nickname;
        private int winAmount;
        private String winHandType;
        private List<String> winCards;
    }

    @Data
    public static class PlayerSettleInfo {
        private Long playerId;
        private String nickname;
        private int chipsAfter;
        private int totalBet;
        private int winAmount; // 新增，负数为输
        private String status;
        private List<String> hand;
    }
}