package com.dnui.poker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:46
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
@Entity
@Data
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date startTime;
}
