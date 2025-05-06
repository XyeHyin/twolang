package com.dnui.poker.facade;

import com.dnui.poker.dto.GameResultVO;
import com.dnui.poker.dto.TableStatusVO;

public interface GameFacade {
    void startGame(Long tableId);
    void playerAction(Long playerId, String action, int amount);
    TableStatusVO getTableStatus(Long tableId);
    GameResultVO getGameResult(Long tableId);
    void joinTable(Long playerId, Long tableId);
    void leaveTable(Long playerId, Long tableId);
    Long createTable(String tableName, int maxPlayers, String playType);
    void closeTable(Long tableId);
}