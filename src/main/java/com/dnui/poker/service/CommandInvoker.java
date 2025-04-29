package com.dnui.poker.service;

import com.dnui.poker.command.Command;
import org.springframework.stereotype.Service;

import java.util.Stack;

/**
 * 命令调用者，负责执行命令和管理命令历史
 */
@Service
public class CommandInvoker {
    private final Stack<Command> history = new Stack<>();

    /**
     * 执行单个命令并记录历史
     */
    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }

    /**
     * 撤销上一个命令（如需支持撤销功能，可扩展Command接口）
     */
    public void undo() {
        if (!history.isEmpty()) {
            // Command last = history.pop();
            // last.undo();
        }
    }

    /**
     * 下注轮流程接口（实际开发中由前端逐步驱动，每次操作都调用GameService.handlePlayerAction）
     * 这里不做任何自动循环，仅保留接口以兼容模板方法调用
     */
    public void executeBettingRound() {
        // 实际开发中无需实现，下注轮由前端逐步驱动
    }

    /**
     * 获取命令历史（只读）
     */
    public Stack<Command> getHistory() {
        return (Stack<Command>) history.clone();
    }
}
