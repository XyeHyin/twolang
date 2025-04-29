package com.dnui.poker.observer;

import com.dnui.poker.event.GameEvent;
import com.dnui.poker.dto.GameResultVO;
import com.dnui.poker.websocket.GameWebSocketService;
import com.dnui.poker.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GameEventListener {
    @Autowired
    private GameWebSocketService wsService;
    @Autowired
    private GameService gameService;

    @EventListener
    public void onGameEvent(GameEvent event) {
        if ("settle".equals(event.getType())) {
            // 获取结算详情VO
            GameResultVO resultVO = gameService.buildGameResultVO(event.getTableId());
            wsService.sendGameEvent(event.getTableId(), "settle", resultVO);
        } else if ("action".equals(event.getType())) {
            // 推送玩家操作（可自定义DTO）
            wsService.sendGameEvent(event.getTableId(), "action", event.getPayload());
        } else {
            wsService.sendGameEvent(event.getTableId(), event.getType(), null);
        }
    }
}