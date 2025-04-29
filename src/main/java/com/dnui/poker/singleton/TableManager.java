package com.dnui.poker.singleton;

import com.dnui.poker.entity.GameSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局唯一的牌桌/房间管理器
 * 单例模式（懒汉式，线程安全）
 */
public class TableManager {
    private static volatile TableManager instance;
    private final Map<Long, GameSession> tableMap = new ConcurrentHashMap<>();

    private TableManager() {}

    public static TableManager getInstance() {
        if (instance == null) {
            synchronized (TableManager.class) {
                if (instance == null) {
                    instance = new TableManager();
                }
            }
        }
        return instance;
    }

    public void addTable(Long tableId, GameSession session) {
        tableMap.put(tableId, session);
    }

    public GameSession getTable(Long tableId) {
        return tableMap.get(tableId);
    }

    public void removeTable(Long tableId) {
        tableMap.remove(tableId);
    }

    public Map<Long, GameSession> getAllTables() {
        return tableMap;
    }
}
