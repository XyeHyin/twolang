package com.dnui.poker.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 19:03
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class TableStatusVO {
    private Long tableId;
    private List<PlayerDTO> players;
    private List<String> publicCards; // 例如 ["AS", "KH", "7D"]
    private int pot; // 当前底池
    private String gamePhase; // "preflop", "flop", "turn", "river", "showdown"
}
