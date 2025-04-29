package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * 加注命令（可用于2-bet/3-bet等）
 */
public class RaiseCommand implements Command {
    private final Long playerId;
    private final int amount;
    private final PlayerService playerService;

    public RaiseCommand(Long playerId, int amount, PlayerService playerService) {
        this.playerId = playerId;
        this.amount = amount;
        this.playerService = playerService;
    }

    @Override
    public void execute() {
        playerService.raise(playerId, amount);
    }
}