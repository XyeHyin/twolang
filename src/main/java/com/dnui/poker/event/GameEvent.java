package com.dnui.poker.event;

import org.springframework.context.ApplicationEvent;

/**
 * 游戏事件，支持Spring事件机制
 */
public class GameEvent extends ApplicationEvent {
    private final Long tableId;
    private final String type;
    private final Object payload;

    /**
     * 构造事件（无payload）
     */
    public GameEvent(Object source, Long tableId, String type) {
        this(source, tableId, type, null);
    }

    /**
     * 构造事件
     * @param source 事件源
     * @param tableId 牌桌ID
     * @param type 事件类型
     * @param payload 附加数据
     */
    public GameEvent(Object source, Long tableId, String type, Object payload) {
        super(source);
        this.tableId = tableId;
        this.type = type;
        this.payload = payload;
    }

    public Long getTableId() { return tableId; }
    public String getType() { return type; }
    public Object getPayload() { return payload; }
}