package com.dnui.poker.factory;

import com.dnui.poker.entity.GameSession;

public class TableFactory {
    public static GameSession createTable(Long tableId, String tableName) {
        GameSession session = new GameSession();
        session.setId(tableId);
        session.setTableName(tableName);
        // 其他初始化逻辑
        return session;
    }
}