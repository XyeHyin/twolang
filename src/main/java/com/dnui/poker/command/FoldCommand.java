package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * FoldCommand
 * 弃牌命令，命令模式实现
 */
public class FoldCommand implements Command {
    /** 玩家ID */
    private final Long playerId;
    /** 玩家服务 */
    private final PlayerService playerService;

    /**
     * 构造弃牌命令
     * @param playerId 玩家ID
     * @param playerService 玩家服务
     */
    public FoldCommand(Long playerId, PlayerService playerService) {
        this.playerId = playerId;
        this.playerService = playerService;
    }

    /**
     * 执行弃牌操作
     */
    @Override
    public void execute() {
        playerService.fold(playerId);
    }
}
