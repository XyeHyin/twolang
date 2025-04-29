package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * BetCommand
 * 下注命令，命令模式实现
 * 封装玩家下注操作，便于解耦业务与控制层
 */
public class BetCommand implements Command {
    /** 玩家ID */
    private final Long playerId;
    /** 下注金额 */
    private final int amount;
    /** 玩家服务 */
    private final PlayerService playerService;

    /**
     * 构造下注命令
     * @param playerId 玩家ID
     * @param amount 下注金额
     * @param playerService 玩家服务
     */
    public BetCommand(Long playerId, int amount, PlayerService playerService) {
        this.playerId = playerId;
        this.amount = amount;
        this.playerService = playerService;
    }

    /**
     * 执行下注操作
     */
    @Override
    public void execute() {
        playerService.bet(playerId, amount);
    }
}

