package com.dnui.poker.facade;

import com.dnui.poker.dto.GameResultVO;
import com.dnui.poker.dto.TableStatusVO;
import com.dnui.poker.entity.GameSession;
import com.dnui.poker.service.GameService;
import com.dnui.poker.service.PlayerService;
import com.dnui.poker.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameFacadeImpl implements GameFacade {
    @Autowired
    private GameService gameService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private TableService tableService;

    @Override
    public void startGame(Long tableId) {
        gameService.startGame(tableId);
    }

    @Override
    public void playerAction(Long playerId, String action, int amount) {
        gameService.handlePlayerAction(playerId, action, amount);
    }

    @Override
    public TableStatusVO getTableStatus(Long tableId) {
        return gameService.buildTableStatusVO(tableId);
    }

    @Override
    public GameResultVO getGameResult(Long tableId) {
        return gameService.buildGameResultVO(tableId);
    }

    @Override
    public void joinTable(Long playerId, Long tableId) {
        playerService.joinGame(playerId, tableId);
    }

    @Override
    public void leaveTable(Long playerId, Long tableId) {
        playerService.leaveGame(playerId);
    }

    @Override
    public Long createTable(String tableName, int maxPlayers, String playType) {
        GameSession session = tableService.createTable(tableName, maxPlayers, playType);
        return session.getId();
    }

    @Override
    public void closeTable(Long tableId) {
        tableService.closeTable(tableId);
    }
}