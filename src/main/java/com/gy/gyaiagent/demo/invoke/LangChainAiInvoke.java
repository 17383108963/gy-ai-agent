package com.gy.gyaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

/**
 * @author gyy
 * @version 1.1
 */
public class LangChainAiInvoke {
    public static void main(String[] args) {
        QwenChatModel qwenChatModel = QwenChatModel.builder()
                .apiKey(TestApiKey.ApiKey)
                .modelName("qwen-plus")
                .build();
        String result = qwenChatModel.chat("你好，我是gy，正在学习Java");
        System.out.println(result);
    }
}
