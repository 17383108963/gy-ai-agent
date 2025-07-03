package com.gy.gyaiagent.app;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.gy.gyaiagent.advisor.MyCustomAdvidor;
import com.gy.gyaiagent.chatmemory.DatabaseChatMemory;
import com.gy.gyaiagent.rag.QueryRewriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

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

    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
    }


    @jakarta.annotation.Resource
    private VectorStore novelAppVectorStore;

    @jakarta.annotation.Resource
    private Advisor novelAppRagCloudAdvisor;

    @jakarta.annotation.Resource
    private VectorStore pgVectorVectorStore;

    @jakarta.annotation.Resource
    private QueryRewriter queryRewriter;

    /**
     * 和 RAG知识库进行对话
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {

        //执行查询重写
//        String queryRewrite = queryRewriter.doQueryRewrite(message);

        ChatResponse response = chatClient
                .prompt()
                // 重写后的查询语句
//                .user(queryRewrite)
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                //启用本地RAG知识库问答
                //.advisors(new QuestionAnswerAdvisor(novelAppVectorStore))
                //应用 RAG 检索增强服务 （基于云知识库向量存储）
                .advisors(novelAppRagCloudAdvisor)
                //应用 RAG 检索增强服务 （基于 PgVector 向量存储）
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
//        log.info("content: {}", content);
        return content;
    }

    @jakarta.annotation.Resource
    private ToolCallback[] allTools;

    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志
                .advisors(new MyCustomAdvidor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

}

