package com.gy.gyaiagent.chatmemory;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gy.gyaiagent.converter.MessageConverter;
import com.gy.gyaiagent.model.entity.ChatMessage;
import com.gy.gyaiagent.service.ChatMessageService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@DS("mysql")
public class DatabaseChatMemory implements ChatMemory {

    private final ChatMessageService chatMessageService;



    @Override
    public void add(String conversationId, List<Message> messages) {
        List<ChatMessage> chatMessages = messages.stream()
                .map(message -> MessageConverter.toChatMessage(message, conversationId))
                .collect(Collectors.toList());

        chatMessageService.saveBatch(chatMessages, chatMessages.size());
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        // 查询最近的 lastN 条消息
        queryWrapper.eq(ChatMessage::getConversationId, conversationId)
                   .orderByDesc(ChatMessage::getCreateTime)
                   .last(lastN > 0, "LIMIT " + lastN);
        
        List<ChatMessage> chatMessages = chatMessageService.list(queryWrapper);
        
        // 按照时间顺序返回
        if (!chatMessages.isEmpty()) {
            Collections.reverse(chatMessages);
        }

        List<Message> messages = chatMessages
                .stream()
                .map(MessageConverter::toMessage)
                .collect(Collectors.toList());
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);
        chatMessageService.remove(queryWrapper);
    }

}
