package com.dnui.poker.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:47
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
public class GameObserver {
    private final List<GameEventListener> listeners = new ArrayList<>();

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void notifyGameStart(Long tableId) {
        for (GameEventListener listener : listeners) {
            listener.onGameStart(tableId);
        }
    }

    public void notifyGameSettle(Long tableId) {
        for (GameEventListener listener : listeners) {
            listener.onGameSettle(tableId);
        }
    }
}

// 事件监听接口
interface GameEventListener {
    void onGameStart(Long tableId);
    void onGameSettle(Long tableId);
}
