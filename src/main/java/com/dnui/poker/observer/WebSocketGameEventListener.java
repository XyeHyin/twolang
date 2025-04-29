package com.dnui.poker.observer;

public class WebSocketGameEventListener implements GameEventListener {
    @Override
    public void onGameStart(Long tableId) {
        // 推送“游戏开始”消息到前端
        System.out.println("WebSocket推送: 游戏开始, tableId=" + tableId);
    }

    @Override
    public void onGameSettle(Long tableId) {
        // 推送“结算”消息到前端
        System.out.println("WebSocket推送: 结算, tableId=" + tableId);
    }

    @Override
    public void onDealCard(Long tableId) {
        // 推送“发牌”消息到前端
        System.out.println("WebSocket推送: 发牌, tableId=" + tableId);
    }
}