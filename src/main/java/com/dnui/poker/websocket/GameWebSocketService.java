package com.dnui.poker.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameWebSocketService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendGameEvent(Long tableId, String eventType, Object payload) {
        messagingTemplate.convertAndSend("/topic/game/" + tableId, new GameWsMessage(eventType, payload));
    }

    public static class GameWsMessage {
        public String type;
        public Object data;
        public GameWsMessage(String type, Object data) {
            this.type = type;
            this.data = data;
        }
    }
}