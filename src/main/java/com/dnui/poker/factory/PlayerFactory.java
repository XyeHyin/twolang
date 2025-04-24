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
    public static Player createPlayer(String nickname) {
        Player player = new Player();
        player.setNickname(nickname);
        player.setChips(1000);
        player.setOnline(true);
        return player;
    }
}
