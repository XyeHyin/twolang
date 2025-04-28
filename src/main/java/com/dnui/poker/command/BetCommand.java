package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * 下注命令
 */
public class BetCommand implements Command {
    private final Long playerId;
    private final int amount;
    private final PlayerService playerService;

    public BetCommand(Long playerId, int amount, PlayerService playerService) {
        this.playerId = playerId;
        this.amount = amount;
        this.playerService = playerService;
    }

    @Override
    public void execute() {
        playerService.bet(playerId, amount);
    }
}

