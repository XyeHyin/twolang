package com.dnui.poker.controller;

import com.dnui.poker.facade.GameFacade;
import com.dnui.poker.dto.GameResultVO;
import com.dnui.poker.dto.TableStatusVO;
import com.dnui.poker.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:43
 * @packageName:IntelliJ IDEA
 * @Description: 牌局相关接口
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    private GameFacade gameFacade;

    @PostMapping("/start")
    public Result<String> startGame(@RequestParam Long tableId) {
        try {
            gameFacade.startGame(tableId);
            return Result.ok("Game started");
        } catch (Exception e) {
            return Result.fail("启动牌局失败: " + e.getMessage());
        }
    }

    @PostMapping("/action")
    public Result<String> playerAction(@RequestParam Long playerId, @RequestParam String action, @RequestParam int amount) {
        try {
            gameFacade.playerAction(playerId, action, amount);
            return Result.ok("Action processed");
        } catch (Exception e) {
            return Result.fail("操作失败: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public Result<TableStatusVO> getGameStatus(@RequestParam Long tableId) {
        try {
            return Result.ok(gameFacade.getTableStatus(tableId));
        } catch (Exception e) {
            return Result.fail("获取状态失败: " + e.getMessage());
        }
    }

    @GetMapping("/result")
    public Result<GameResultVO> getGameResult(@RequestParam Long tableId) {
        try {
            return Result.ok(gameFacade.getGameResult(tableId));
        } catch (Exception e) {
            return Result.fail("获取结算失败: " + e.getMessage());
        }
    }

    @PostMapping("/settle")
    public Result<String> forceSettle(@RequestParam Long tableId) {
        try {
            gameFacade.getGameResult(tableId); // 或直接调用结算
            return Result.ok("Settle triggered");
        } catch (Exception e) {
            return Result.fail("结算失败: " + e.getMessage());
        }
    }

    // 新增：创建房间
    @PostMapping("/createTable")
    public Result<Long> createTable(@RequestParam String tableName, @RequestParam int maxPlayers, @RequestParam String playType) {
        try {
            Long tableId = gameFacade.createTable(tableName, maxPlayers, playType);
            return Result.ok(tableId);
        } catch (Exception e) {
            return Result.fail("创建房间失败: " + e.getMessage());
        }
    }

    // 新增：关闭房间
    @PostMapping("/closeTable")
    public Result<String> closeTable(@RequestParam Long tableId) {
        try {
            gameFacade.closeTable(tableId);
            return Result.ok("Table closed");
        } catch (Exception e) {
            return Result.fail("关闭房间失败: " + e.getMessage());
        }
    }

    // 新增：玩家加入房间
    @PostMapping("/joinTable")
    public Result<String> joinTable(@RequestParam Long playerId, @RequestParam Long tableId) {
        try {
            gameFacade.joinTable(playerId, tableId);
            return Result.ok("joined");
        } catch (Exception e) {
            return Result.fail("加入房间失败: " + e.getMessage());
        }
    }

    // 新增：玩家离开房间
    @PostMapping("/leaveTable")
    public Result<String> leaveTable(@RequestParam Long playerId, @RequestParam Long tableId) {
        try {
            gameFacade.leaveTable(playerId, tableId);
            return Result.ok("left");
        } catch (Exception e) {
            return Result.fail("离开房间失败: " + e.getMessage());
        }
    }
}
