package com.dnui.poker.controller;

import com.dnui.poker.entity.Player;
import com.dnui.poker.service.PlayerService;
import com.dnui.poker.util.Result;
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
    public Result<Player> createPlayer(@RequestParam String nickname) {
        return Result.ok(playerService.createPlayer(nickname));
    }

    @PostMapping("/join")
    public Result<String> joinRoom(@RequestParam Long playerId, @RequestParam Long tableId) {
        playerService.joinGame(playerId, tableId);
        return Result.ok("joined");
    }

    @PostMapping("/leave")
    public Result<String> leaveRoom(@RequestParam Long playerId, @RequestParam Long tableId) {
        playerService.leaveGame(playerId);
        return Result.ok("left");
    }
}