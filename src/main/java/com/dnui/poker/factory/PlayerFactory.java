package com.dnui.poker.factory;

import com.dnui.poker.entity.Player;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:47
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
public class PlayerFactory {
    public static Player createPlayer(Long playerId, String nickname, int chips) {
        Player player = new Player();
        player.setId(playerId);
        player.setNickname(nickname);
        player.setChips(chips);
        // 其他初始化
        return player;
    }
}
