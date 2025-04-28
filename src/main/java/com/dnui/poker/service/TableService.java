package com.dnui.poker.service;

import com.dnui.poker.entity.GameSession;
import org.springframework.stereotype.Service;

@Service
public class TableService {
    public GameSession createTable() {
        // 创建房间/桌子逻辑
    }
    public void closeTable(Long tableId) {
        // 销毁房间/桌子逻辑
    }
    public GameSession getActiveSession(Long tableId) {
        // 查询当前活跃牌局
    }
}