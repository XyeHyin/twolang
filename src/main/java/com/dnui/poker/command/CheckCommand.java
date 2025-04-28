package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * 过牌命令
 */
public class CheckCommand implements Command {
    private final Long playerId;
    private final PlayerService playerService;

    public CheckCommand(Long playerId, PlayerService playerService) {
        this.playerId = playerId;
        this.playerService = playerService;
    }

    @Override
    public void execute() {
        playerService.check(playerId);
    }
}