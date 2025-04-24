package com.dnui.poker.service;

import com.dnui.poker.entity.Player;
import com.dnui.poker.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:44
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public Player createPlayer(String nickname) {
        Player player = new Player();
        player.setNickname(nickname);
        player.setChips(1000);
        player.setOnline(true);
        return playerRepository.save(player);
    }
}
