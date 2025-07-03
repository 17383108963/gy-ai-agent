package com.gy.gyaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.gy.gyaiagent.enums.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return SseEmitter实例
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建SseEmitter，设置较长的超时时间
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentState.IDLE) {
                    emitter.send("错误：无法从状态运行代理: " + this.state);
                    emitter.complete();
                    return;
                }
                if (StringUtil.isBlank(userPrompt)) {
                    emitter.send("错误：不能使用空提示词运行代理");
                    emitter.complete();
                    return;
                }

                // 更改状态
                state = AgentState.RUNNING;
                // 记录消息上下文
                messageList.add(new UserMessage(userPrompt));

                try {
                    for (int i = 0; i < maxStep && state != AgentState.FINISHED; i++) {
                        int stepNumber = i + 1;
                        currentStep = stepNumber;
                        log.info("Executing step " + stepNumber + "/" + maxStep);

                        // 单步执行
                        String stepResult = step();
                        String result = "Step " + stepNumber + ": " + stepResult;

                        // 发送每一步的结果
                        emitter.send(result);
                    }
                    // 检查是否超出步骤限制
                    if (currentStep >= maxStep) {
                        state = AgentState.FINISHED;
                        emitter.send("执行结束: 达到最大步骤 (" + maxStep + ")");
                    }
                    // 正常完成
                    emitter.complete();
                } catch (Exception e) {
                    state = AgentState.ERROR;
                    log.error("执行智能体失败", e);
                    try {
                        emitter.send("执行错误: " + e.getMessage());
                        emitter.complete();
                    } catch (Exception ex) {
                        emitter.completeWithError(ex);
                    }
                } finally {
                    // 清理资源
                    this.cleanup();
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        // 设置超时和完成回调
        emitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timed out");
        });

        emitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });

        return emitter;
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
