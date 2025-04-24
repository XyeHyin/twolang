package com.dnui.poker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:46
 * @packageName:IntelliJ IDEA
 * @Description: 牌局/房间实体
 * @Version: 1.0
 */
@Entity
@Data
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date startTime;
     
    @OneToMany(mappedBy = "gameSession")
    private List<Player> players;

    private int pot;
    @ManyToOne
    private Player smallBlindPlayer;

    private int currentRound; // 0-翻牌前, 1-翻牌, 2-转牌, 3-河牌

    @ManyToOne
    private Player currentActionPlayer;

    
    
}
