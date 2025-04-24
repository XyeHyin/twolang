package com.dnui.poker.controller;

import com.dnui.poker.entity.Player;
import com.dnui.poker.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:43
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/player")
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @PostMapping("/create")
    public Player createPlayer(@RequestParam String nickname) {
        return playerService.createPlayer(nickname);
    }

    @PostMapping("/join")
    public String joinRoom(@RequestParam Long playerId, @RequestParam Long tableId) {
        // 加入房间逻辑
        return "joined";
    }

    @PostMapping("/leave")
    public String leaveRoom(@RequestParam Long playerId, @RequestParam Long tableId) {
        // 离开房间逻辑
        return "left";
    }
}