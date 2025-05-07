package com.dnui.poker.service;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.repository.GameSessionRepository;
import com.dnui.poker.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:44
 * @packageName:IntelliJ IDEA
 * @Description: 玩家相关操作服务
 * @Version: 1.0
 */
@Service
@Transactional
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameSessionRepository gameSessionRepository;

    // 玩家注册
    public Player createPlayer(String nickname) {
        Player player = new Player();
        player.setNickname(nickname);
        player.setChips(10000);
        player.setOnline(true);
        player.setRegisterTime(new Date());
        player.setStatus(Player.PlayerStatus.WAITING);
        return playerRepository.save(player); // repository落地
    }

    // 玩家加入房间
    public void joinGame(Long playerId, Long gameSessionId) {
        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("玩家不存在: " + playerId));
        GameSession session = gameSessionRepository.findById(gameSessionId)
            .orElseThrow(() -> new IllegalArgumentException("房间不存在: " + gameSessionId));
        // 直接查数据库，获取当前房间所有已占用的座位号
        List<Player> playersInSession = playerRepository.findByGameSession(session);
        int seatNumber = 1;
        while (true) {
            boolean taken = false;
            for (Player p : playersInSession) {
                if (p.getSeatNumber() != null && p.getSeatNumber() == seatNumber) {
                    taken = true;
                    seatNumber++;
                    break;
                }
            }
            if (!taken) break;
        }
        player.setGameSession(session);
        player.setSeatNumber(seatNumber);
        player.setStatus(Player.PlayerStatus.WAITING);
        playerRepository.save(player);
    }

    // 玩家加入房间（带座位号）
    public void joinGame(Long playerId, Long gameSessionId, int seatNumber) {
        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("玩家不存在: " + playerId));
        GameSession session = gameSessionRepository.findById(gameSessionId)
            .orElseThrow(() -> new IllegalArgumentException("房间不存在: " + gameSessionId));
        // 校验座位是否被占用
        boolean seatTaken = session.getPlayers() != null && session.getPlayers().stream()
            .anyMatch(p -> p.getSeatNumber() == seatNumber);
        if (seatTaken) {
            throw new IllegalArgumentException("该座位已被占用");
        }
        player.setGameSession(session);
        player.setSeatNumber(seatNumber);
        player.setStatus(Player.PlayerStatus.WAITING);
        playerRepository.save(player);
    }

    // 玩家离开房间
    public void leaveGame(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        player.setGameSession(null);
        player.setSeatNumber(null);
        player.setStatus(Player.PlayerStatus.WAITING);
        playerRepository.save(player);
    }

    // 下注
    public void bet(Long playerId, int amount) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        if (player.getChips() < amount) throw new IllegalArgumentException("筹码不足");
        player.setChips(player.getChips() - amount);
        player.setBetChips(player.getBetChips() + amount);
        player.setTotalBetChips(player.getTotalBetChips() + amount);
        player.setStatus(Player.PlayerStatus.ACTIVE);

        // 新增：累加到底池
        GameSession session = player.getGameSession();
        if (session != null) {
            session.setPot(session.getPot() + amount);
        }

        playerRepository.save(player);
        if (session != null) {
            // 确保底池变动落库
            gameSessionRepository.save(session);
        }
    }

    // 加注
    public void raise(Long playerId, int amount) {
        bet(playerId, amount);
    }

    // 跟注
    public void call(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        GameSession session = player.getGameSession();
        int maxBet = session.getPlayers().stream()
                .mapToInt(Player::getBetChips)
                .max().orElse(0);
        int toCall = maxBet - player.getBetChips();
        if (player.getChips() < toCall) throw new IllegalArgumentException("筹码不足以跟注");
        bet(playerId, toCall);
         player.setStatus(Player.PlayerStatus.ACTIVE);
    playerRepository.save(player);
    }

    // 弃牌
    public void fold(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        player.setStatus(Player.PlayerStatus.FOLDED);
        playerRepository.save(player); // repository落地
        // 可选：如需彻底移除玩家手牌，可在此处清理PlayerHand
        // playerHandRepository.deleteByPlayer(player);
    }

    // 过牌
    public void check(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        GameSession session = player.getGameSession();
        int maxBet = session.getPlayers().stream()
                .mapToInt(Player::getBetChips)
                .max().orElse(0);
        if (maxBet > 0 && player.getBetChips() < maxBet) {
            throw new IllegalStateException("当前不能过牌，只能跟注或弃牌");
        }
        player.setStatus(Player.PlayerStatus.ACTIVE);
        playerRepository.save(player); // repository落地
    }

    // 全下
    public void allIn(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        int allInAmount = player.getChips();
        if (allInAmount <= 0) throw new IllegalArgumentException("玩家没有可用筹码");
        player.setBetChips(player.getBetChips() + allInAmount);
        player.setTotalBetChips(player.getTotalBetChips() + allInAmount);
        player.setChips(0);
        player.setStatus(Player.PlayerStatus.ALL_IN);
        playerRepository.save(player); // repository落地
    }

    // 获取玩家所在牌局
    public GameSession getPlayerSession(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        return player.getGameSession();
    }

    // 查询玩家
    public Optional<Player> findByNickname(String nickname) {
        return playerRepository.findByNickname(nickname);
    }

    // 重置玩家状态
    public void resetPlayers(GameSession session) {
        if (session == null || session.getPlayers() == null) return;
        for (Player p : session.getPlayers()) {
            p.setStatus(Player.PlayerStatus.ACTIVE);
            p.setBetChips(0);
        }
    }

    // 结束一轮，清理玩家状态
    public void finishRound(com.dnui.poker.entity.GameSession session) {
        if (session == null || session.getPlayers() == null) return;
        for (com.dnui.poker.entity.Player p : session.getPlayers()) {
            p.setBetChips(0);
        }
    }

    // 填充机器人
    public void fillRobotsIfNeeded(Long gameSessionId) {
    }

    /**
     * 补机器人到指定座位
     */
    public void fillRobot(Long tableId, int seatNumber) {
        GameSession session = gameSessionRepository.findById(tableId)
            .orElseThrow(() -> new IllegalArgumentException("房间不存在: " + tableId));
        List<Player> players = playerRepository.findByGameSession(session);
        boolean seatTaken = players.stream().anyMatch(p -> p.getSeatNumber() != null && p.getSeatNumber() == seatNumber);
        if (seatTaken) {
            throw new IllegalArgumentException("该座位已被占用");
        }

        // 全局查找所有机器人昵称
        List<Player> allPlayers = playerRepository.findAll();
        Set<String> usedNicknames = new HashSet<>();
        for (Player p : allPlayers) {
            if (p.getNickname() != null && p.getNickname().startsWith("机器人")) {
                usedNicknames.add(p.getNickname());
            }
        }
        int robotIndex = 1;
        String robotNickname;
        do {
            robotNickname = "机器人" + robotIndex;
            robotIndex++;
        } while (usedNicknames.contains(robotNickname));

        Player robot = new Player();
        robot.setNickname(robotNickname);
        robot.setChips(10000);
        robot.setOnline(true);
        robot.setRegisterTime(new Date());
        robot.setStatus(Player.PlayerStatus.WAITING);
        robot.setGameSession(session);
        robot.setSeatNumber(seatNumber);
        playerRepository.save(robot);
    }
}
