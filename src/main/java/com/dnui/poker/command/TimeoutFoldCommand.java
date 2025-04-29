package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

public class TimeoutFoldCommand implements Command {
    private final Long playerId;
    private final PlayerService playerService;

    public TimeoutFoldCommand(Long playerId, PlayerService playerService) {
        this.playerId = playerId;
        this.playerService = playerService;
    }

    @Override
    public void execute() {
        playerService.fold(playerId);
    }
}