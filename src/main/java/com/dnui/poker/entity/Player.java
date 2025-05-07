package com.dnui.poker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(
    name = "player",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nickname"}),
        @UniqueConstraint(columnNames = {"game_session_id", "seat_number"})
    }
)
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String nickname;

    private int chips;

    @Column(name = "seat_number") // 不要加nullable = false
    private Integer seatNumber;

    private boolean online;

    private int betChips;
    private int totalBetChips;

    private String avatar;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registerTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @Enumerated(EnumType.STRING)
    private PlayerStatus status;

   public enum PlayerStatus {
    WAITING, // 等待新局
    WAITING_FOR_ACTION, // 当前应操作
    ACTIVE, // 已操作
    FOLDED, // 弃牌
    ALL_IN, // 全下
    SETTLED // 已结算
}
}