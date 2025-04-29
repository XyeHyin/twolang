package com.dnui.poker.observer;

/**
 * WebSocket 游戏事件监听器
 * 实际项目可集成 WebSocket 推送
 */
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

    @Override
    public void onGameEnd(Long tableId) {
        // 推送“游戏结束”消息到前端
        System.out.println("WebSocket推送: 游戏结束, tableId=" + tableId);
    }
}