package com.dnui.poker.service;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.repository.GameSessionRepository;
import com.dnui.poker.singleton.TableManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableService {
    @Autowired
    private GameSessionRepository gameSessionRepository;

    public GameSession createTable(String tableName, int maxPlayers, String playType) {
        GameSession session = new GameSession();
        session.setTableName(tableName);
        session.setMaxPlayers(maxPlayers);
        session.setPlayType(playType);
        session.setActive(true);
        session.setPhase(com.dnui.poker.dto.GamePhase.PRE_FLOP);
        GameSession saved = gameSessionRepository.save(session);
        TableManager.getInstance().addTable(saved.getId(), saved);
        return saved;
    }

    public void closeTable(Long tableId) {
        GameSession session = gameSessionRepository.findById(tableId).orElseThrow();
        session.setActive(false);
        gameSessionRepository.save(session);
        TableManager.getInstance().removeTable(tableId);
    }

    public GameSession getActiveSession(Long tableId) {
        // 单例模式：优先从TableManager获取
        GameSession session = TableManager.getInstance().getTable(tableId);
        if (session != null && session.isActive()) {
            return session;
        }
        return gameSessionRepository.findById(tableId)
                .filter(GameSession::isActive)
                .orElse(null);
    }
}