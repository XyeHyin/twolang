package com.dnui.poker.observer;

import com.dnui.poker.event.GameEvent;
import com.dnui.poker.dto.GameResultVO;
import com.dnui.poker.websocket.GameWebSocketService;
import com.dnui.poker.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 游戏事件监听器接口
 * 支持多种事件类型，便于事件广播
 */
public interface GameEventListener {
    /**
     * 游戏开始事件
     * @param tableId 牌桌ID
     */
    void onGameStart(Long tableId);

    /**
     * 游戏结算事件
     * @param tableId 牌桌ID
     */
    void onGameSettle(Long tableId);

    /**
     * 发牌事件
     * @param tableId 牌桌ID
     */
    void onDealCard(Long tableId);

    /**
     * 游戏结束事件
     * @param tableId 牌桌ID
     */
    void onGameEnd(Long tableId);
}

