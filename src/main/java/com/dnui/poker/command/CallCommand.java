package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * 命令模式：跟注命令
 */
public class CallCommand implements Command {
    private final Long playerId;
    private final PlayerService playerService;

    public CallCommand(Long playerId, PlayerService playerService) {
        this.playerId = playerId;
        this.playerService = playerService;
    }

    @Override
    public void execute() {
        playerService.call(playerId);
    }
}