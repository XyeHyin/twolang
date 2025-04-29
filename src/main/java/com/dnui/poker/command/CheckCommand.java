package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * CheckCommand
 * 过牌命令，命令模式实现
 */
public class CheckCommand implements Command {
    /** 玩家ID */
    private final Long playerId;
    /** 玩家服务 */
    private final PlayerService playerService;

    /**
     * 构造过牌命令
     * @param playerId 玩家ID
     * @param playerService 玩家服务
     */
    public CheckCommand(Long playerId, PlayerService playerService) {
        this.playerId = playerId;
        this.playerService = playerService;
    }

    /**
     * 执行过牌操作
     */
    @Override
    public void execute() {
        playerService.check(playerId);
    }
}