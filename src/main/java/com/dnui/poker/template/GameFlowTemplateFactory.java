package com.dnui.poker.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameFlowTemplateFactory {
    private final Map<String, GameFlowTemplate> templateMap = new ConcurrentHashMap<>();

    @Autowired
    public GameFlowTemplateFactory(List<GameFlowTemplate> templates) {
        for (GameFlowTemplate template : templates) {
            templateMap.put(template.getPlayType(), template);
        }
    }

    public GameFlowTemplate getTemplate(String playType) {
        GameFlowTemplate template = templateMap.get(playType);
        if (template == null) throw new IllegalArgumentException("不支持的玩法类型: " + playType);
        return template;
    }
}