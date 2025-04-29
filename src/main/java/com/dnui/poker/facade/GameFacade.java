package com.dnui.poker.facade;

import com.dnui.poker.dto.GameResultVO;
import com.dnui.poker.dto.TableStatusVO;

public interface GameFacade {
    void startGame(Long tableId);
    void playerAction(Long playerId, String action, int amount);
    TableStatusVO getTableStatus(Long tableId);
    GameResultVO getGameResult(Long tableId);
}