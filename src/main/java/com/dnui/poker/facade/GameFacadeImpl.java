package com.dnui.poker.facade;

import com.dnui.poker.dto.GameResultVO;
import com.dnui.poker.dto.TableStatusVO;
import com.dnui.poker.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameFacadeImpl implements GameFacade {
    @Autowired
    private GameService gameService;

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
}