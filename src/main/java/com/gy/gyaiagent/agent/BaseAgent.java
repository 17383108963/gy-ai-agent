package com.gy.gyaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.gy.gyaiagent.enums.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gyy
 * @version 1.1
 * 抽象代理基础类，用于管理代理状态和执行流程
 * 提供状态转换、内存管理和基于步骤的循环执行的基础功能
 * 子类必须实现step方法
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示
    private String systemPrompt;
    private String nextStepPrompt;

    // 状态
    private AgentState state = AgentState.IDLE;

    // 执行控制
    private int maxStep = 10;
    private int currentStep = 0;

    // LLM
    private ChatClient chatClient;

    // Memory(记忆上下文，自主维护)
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt){
        if(this.state != AgentState.IDLE){
            throw new RuntimeException("Cannot run agent from state: " + state);
        }
        if (StringUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty userPrompt");
        }
        // 更改状态
        this.state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表
        List<String> results = new ArrayList<>();

        try{
            for (int i = 0; i < maxStep && state != AgentState.FINISHED; i++) {
                int stepNumber = i+1;
                currentStep = stepNumber;
                log.info("Executing step: " + stepNumber + "/" + maxStep);
                // 单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            // 检查是否超出步骤
            if (currentStep >= maxStep){
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max Steps (" + maxStep + ")");
            }
            return String.join("\n", results);

        }catch (Exception e){
            state = AgentState.ERROR;
            log.error("Error executing agent",e);
            return "执行错误" + e.getMessage();
        }finally {
            // 清理资源
            this.cleanup();
        }

    }


    /**
     * 清理资源
     */
    private void cleanup() {
        //子类可重写该方法清理资源
    }

    /**
     * 执行单步步骤
     * @return 执行结果
     */
    public abstract String step();

}
