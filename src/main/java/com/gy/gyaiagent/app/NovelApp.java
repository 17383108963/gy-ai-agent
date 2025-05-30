package com.gy.gyaiagent.app;

import com.gy.gyaiagent.advisor.MyCustomAdvidor;
import com.gy.gyaiagent.chatmemory.DatabaseChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @author gyy
 * @version 1.1
 */
@Component
@Slf4j
public class NovelApp {

    private final ChatClient chatClient;


    public NovelApp(ChatModel dashscopeChatModel, DatabaseChatMemory databaseChatMemory, @Value("classpath:templates/prompt-reader.txt") Resource systemResource) {
        // 初始化基于文件的对话记忆
//        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
//        FileBasedChatMemory chatMemory = new FileBasedChatMemory(fileDir);
//        // 初始化基于内存的对话记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();
        //初始化基于MySQL数据库的对话记忆

        //从文件加载系统模板预设

        String systemPromptTemplate = new SystemPromptTemplate(systemResource).getTemplate();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemPromptTemplate)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(databaseChatMemory),
                        //自定义advisor,按需开启
                        new MyCustomAdvidor()
                        //自定义推理增强Read2advisor，按需开启
                        //,new ReReadingAdvisor()
                )
                .build();
    }

    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
//        log.info("content: {}", content);
        return content;
    }



}

