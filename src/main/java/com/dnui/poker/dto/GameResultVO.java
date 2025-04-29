package com.dnui.poker.dto;

import lombok.Data;
import java.util.List;

@Data
public class GameResultVO {
    private List<WinnerInfo> winners;      // 所有获胜玩家（支持平分）
    private List<PlayerSettleInfo> allPlayers; // 所有玩家结算信息
    private List<String> publicCards;      // 公共牌（如 ["AS", "KH", "7D", ...]）
    private int totalPot;                  // 总底池
    private String handType;               // 胜者牌型（如"STRAIGHT_FLUSH"）
    private boolean isShowdown;            // 是否摊牌

    @Data
    public static class WinnerInfo {
        private Long playerId;
        private String nickname;
        private int winAmount;
        private String winHandType;
        private List<String> winCards; // ["AS", "KS", "QS", "JS", "10S"]
    }

    @Data
    public static class PlayerSettleInfo {
        private Long playerId;
        private String nickname;
        private int chipsAfter;      // 结算后筹码
        private int totalBet;        // 本局总下注
        private String status;       // "FOLDED"/"ALL_IN"/"ACTIVE"
        private List<String> hand;   // 手牌
    }
}
