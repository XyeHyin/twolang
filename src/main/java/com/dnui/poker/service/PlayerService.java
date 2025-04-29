package com.dnui.poker.service;

import com.dnui.poker.entity.GameSession;
import com.dnui.poker.entity.Player;
import com.dnui.poker.repository.GameSessionRepository;
import com.dnui.poker.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:44
 * @packageName:IntelliJ IDEA
 * @Description: 玩家相关操作服务
 * @Version: 1.0
 */
@Service
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
        Player player = playerRepository.findById(playerId).orElseThrow();
        GameSession session = gameSessionRepository.findById(gameSessionId).orElseThrow();
        player.setGameSession(session);
        player.setStatus(Player.PlayerStatus.WAITING);
        playerRepository.save(player); // repository落地
    }

    // 玩家离开房间
    public void leaveGame(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        player.setGameSession(null);
        player.setStatus(Player.PlayerStatus.WAITING);
        playerRepository.save(player); // repository落地
    }

    // 下注
    public void bet(Long playerId, int amount) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        if (player.getChips() < amount) throw new IllegalArgumentException("筹码不足");
        player.setChips(player.getChips() - amount);
        player.setBetChips(player.getBetChips() + amount);
        player.setTotalBetChips(player.getTotalBetChips() + amount);
        player.setStatus(Player.PlayerStatus.ACTIVE);
        playerRepository.save(player); // repository落地
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
    public void resetPlayers(com.dnui.poker.entity.GameSession session) {
        // 实现：遍历session.getPlayers()，重置状态
        if (session == null || session.getPlayers() == null) return;
        for (com.dnui.poker.entity.Player p : session.getPlayers()) {
            p.setStatus(com.dnui.poker.entity.Player.PlayerStatus.ACTIVE);
            p.setBetChips(0);
            // 其他需要重置的属性
        }
    }

    // 结束一轮，清理玩家状态
    public void finishRound(com.dnui.poker.entity.GameSession session) {
        // 实现：遍历session.getPlayers()，做清理
        if (session == null || session.getPlayers() == null) return;
        for (com.dnui.poker.entity.Player p : session.getPlayers()) {
            // 例如重置下注、状态等
            p.setBetChips(0);
            // 其他清理逻辑
        }
    }
}
