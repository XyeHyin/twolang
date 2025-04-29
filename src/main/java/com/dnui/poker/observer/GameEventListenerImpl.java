package com.dnui.poker.observer;

import com.dnui.poker.event.GameEvent;
import com.dnui.poker.dto.GameResultVO;
import com.dnui.poker.websocket.GameWebSocketService;
import com.dnui.poker.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Spring事件监听器实现，负责监听GameEvent并广播到WebSocket
 */
@Component
public class GameEventListenerImpl implements GameEventListener {
    @Autowired
    private GameWebSocketService wsService;
    @Autowired
    private GameService gameService;

    /**
     * Spring事件监听，自动处理GameEvent
     */
    @EventListener
    public void onGameEvent(GameEvent event) {
        if ("settle".equals(event.getType())) {
            // 获取结算详情VO并推送
            GameResultVO resultVO = gameService.buildGameResultVO(event.getTableId());
            wsService.sendGameEvent(event.getTableId(), "settle", resultVO);
        } else if ("action".equals(event.getType())) {
            // 推送玩家操作
            wsService.sendGameEvent(event.getTableId(), "action", event.getPayload());
        } else if ("start".equals(event.getType())) {
            wsService.sendGameEvent(event.getTableId(), "start", null);
        } else if ("deal".equals(event.getType())) {
            wsService.sendGameEvent(event.getTableId(), "deal", null);
        } else if ("finish".equals(event.getType())) {
            wsService.sendGameEvent(event.getTableId(), "finish", null);
        }
    }

    @Override
    public void onGameStart(Long tableId) {
        wsService.sendGameEvent(tableId, "start", null);
    }

    @Override
    public void onGameSettle(Long tableId) {
        GameResultVO resultVO = gameService.buildGameResultVO(tableId);
        wsService.sendGameEvent(tableId, "settle", resultVO);
    }

    @Override
    public void onDealCard(Long tableId) {
        wsService.sendGameEvent(tableId, "deal", null);
    }

    @Override
    public void onGameEnd(Long tableId) {
        wsService.sendGameEvent(tableId, "finish", null);
    }
}
