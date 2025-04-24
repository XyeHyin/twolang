package com.dnui.poker.controller;

import com.dnui.poker.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:43
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping("/start")
    public String startGame(@RequestParam Long tableId) {
        gameService.startGame(tableId);
        return "Game started";
    }

    @PostMapping("/action")
    public String playerAction(@RequestParam Long playerId, @RequestParam String action, @RequestParam int amount) {
        gameService.handlePlayerAction(playerId, action, amount);
        return "Action processed";
    }
}
