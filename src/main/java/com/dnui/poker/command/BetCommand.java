package com.dnui.poker.command;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:45
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
public class BetCommand implements Command {
    private int amount;

    public BetCommand(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute() {
        // 执行下注逻辑
    }
}

