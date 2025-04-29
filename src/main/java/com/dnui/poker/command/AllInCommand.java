package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * AllInCommand
 * 全下命令，命令模式实现
 */
public class AllInCommand implements Command {
    /** 玩家ID */
    private final Long playerId;
    /** 玩家服务 */
    private final PlayerService playerService;

    /**
     * 构造全下命令
     * @param playerId 玩家ID
     * @param playerService 玩家服务
     */
    public AllInCommand(Long playerId, PlayerService playerService) {
        this.playerId = playerId;
        this.playerService = playerService;
    }

    /**
     * 执行全下操作
     */
    @Override
    public void execute() {
        playerService.allIn(playerId);
    }
}