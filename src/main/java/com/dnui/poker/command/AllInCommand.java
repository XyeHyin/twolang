package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * 全下命令
 */
public class AllInCommand implements Command {
    private final Long playerId;
    private final PlayerService playerService;

    public AllInCommand(Long playerId, PlayerService playerService) {
        this.playerId = playerId;
        this.playerService = playerService;
    }

    @Override
    public void execute() {
        playerService.allIn(playerId);
    }
}