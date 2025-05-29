package com.gy.gyaiagent.converter;

import com.gy.gyaiagent.model.entity.ChatMessage;
import org.springframework.ai.chat.messages.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gyy
 * @version 1.1
 */
public class MessageConverter {

    /**
     * 将 Message 转换为 ChatMessage
     */
    public static ChatMessage toChatMessage(Message message, String conversationId) {
        return ChatMessage.builder()
                .conversationId(conversationId)
                .messageType(message.getMessageType())
                .textContent(message.getText())
                .metaData(message.getMetadata())
                .createTime(new Date())
                .build();
    }

    /**
     * 将 ChatMessage 转换为 Message
     */
    public static Message toMessage(ChatMessage chatMessage) {
        MessageType messageType = chatMessage.getMessageType();
        String text = chatMessage.getTextContent();
        return switch (messageType) {
            case USER -> new UserMessage(text);
            case ASSISTANT -> new AssistantMessage(text);
            case SYSTEM -> new SystemMessage(text);
            case TOOL -> new ToolResponseMessage(List.of());
        };
    }

}
