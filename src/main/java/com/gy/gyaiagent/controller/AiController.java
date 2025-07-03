package com.gy.gyaiagent.controller;

import com.gy.gyaiagent.agent.YuManus;
import com.gy.gyaiagent.app.LoveApp;
import com.gy.gyaiagent.app.NovelApp;
import com.gy.gyaiagent.chatmemory.DatabaseChatMemory;
import com.gy.gyaiagent.context.UserChatId;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private NovelApp novelApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    @Resource
    private DatabaseChatMemory databaseChatMemory;

    @GetMapping("/novel_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return novelApp.doChat(message, chatId);
    }

    @GetMapping(value = "/novel_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return novelApp.doChatByStream(message, chatId);
    }


    @GetMapping("/love_app/chat/sse/emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        novelApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回emitter
        return emitter;
    }


    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        String ChatId = UUID.randomUUID().toString();
        UserChatId.setCtx(ChatId);
        YuManus yuManus = new YuManus(allTools, databaseChatMemory,dashscopeChatModel);
        return yuManus.runStream(message);
    }


}
