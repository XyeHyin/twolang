package com.dnui.poker.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏事件观察者，负责事件广播
 */
public class GameObserver {
    private final List<GameEventListener> listeners = new ArrayList<>();

    /**
     * 注册事件监听器
     */
    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    /**
     * 广播游戏开始事件
     */
    public void notifyGameStart(Long tableId) {
        for (GameEventListener listener : listeners) {
            listener.onGameStart(tableId);
        }
    }

    /**
     * 广播结算事件
     */
    public void notifyGameSettle(Long tableId) {
        for (GameEventListener listener : listeners) {
            listener.onGameSettle(tableId);
        }
    }

    /**
     * 广播发牌事件
     */
    public void notifyDealCard(Long tableId) {
        for (GameEventListener listener : listeners) {
            listener.onDealCard(tableId);
        }
    }

    /**
     * 广播游戏结束事件
     */
    public void notifyGameEnd(Long tableId) {
        for (GameEventListener listener : listeners) {
            listener.onGameEnd(tableId);
        }
    }
}


