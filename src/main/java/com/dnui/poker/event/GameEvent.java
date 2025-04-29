package com.dnui.poker.event;

import org.springframework.context.ApplicationEvent;

public class GameEvent extends ApplicationEvent {
    private final Long tableId;
    private final String type;
    private final Object payload;

    public GameEvent(Object source, Long tableId, String type) {
        this(source, tableId, type, null);
    }

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