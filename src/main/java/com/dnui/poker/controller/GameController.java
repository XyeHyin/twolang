package com.dnui.poker.controller;

import com.dnui.poker.facade.GameFacade;
import com.dnui.poker.vo.GameResultVO;
import com.dnui.poker.vo.TableStatusVO;
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
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    private GameFacade gameFacade;

    @PostMapping("/start")
    public String startGame(@RequestParam Long tableId) {
        gameFacade.startGame(tableId);
        return "Game started";
    }

    @PostMapping("/action")
    public String playerAction(@RequestParam Long playerId, @RequestParam String action, @RequestParam int amount) {
        gameFacade.playerAction(playerId, action, amount);
        return "Action processed";
    }

    @GetMapping("/status")
    public TableStatusVO getGameStatus(@RequestParam Long tableId) {
        return gameFacade.getTableStatus(tableId);
    }

    @GetMapping("/result")
    public GameResultVO getGameResult(@RequestParam Long tableId) {
        return gameFacade.getGameResult(tableId);
    }
}
