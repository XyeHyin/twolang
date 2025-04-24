package com.dnui.poker.command;

import java.util.Stack;

/**
 * @Author: XyeHyin
 * @Date: 2025/4/24 13:45
 * @packageName:IntelliJ IDEA
 * @Description: TODO
 * @Version: 1.0
 */
public class CommandInvoker {
    private final Stack<Command> history = new Stack<>();

    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (!history.isEmpty()) {
            // 可扩展撤销逻辑
        }
    }
}
