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
        WAITING, ACTIVE, FOLDED, ALL_IN, SETTLED
    }
}