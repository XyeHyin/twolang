package com.dnui.poker.command;

import com.dnui.poker.service.PlayerService;

/**
 * 弃牌命令
 */
public class FoldCommand implements Command {
    private final Long playerId;
    private final PlayerService playerService;

    public FoldCommand(Long playerId, PlayerService playerService) {
        this.playerId = playerId;
        this.playerService = playerService;
    }

    @Override
    public void execute() {
        playerService.fold(playerId);
    }
}
