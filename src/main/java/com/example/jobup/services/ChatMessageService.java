package com.example.jobup.services;

import com.example.jobup.entities.ChatMessage;
import com.example.jobup.entities.MessageType;
import com.example.jobup.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    
    private final ChatMessageRepository chatMessageRepository;
    
    public ChatMessage sendTextMessage(String chatId, String senderId, String senderName, String senderType, String content) {
        ChatMessage message = ChatMessage.builder()
                .chatId(chatId)
                .senderId(senderId)
                .senderName(senderName)
                .senderType(senderType)
                .content(content)
                .timestamp(LocalDateTime.now())
                .messageType(MessageType.TEXT)
                .build();
        
        return chatMessageRepository.save(message);
    }
    
    public List<ChatMessage> getChatMessages(String chatId) {
        return chatMessageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }
    
    public String generateChatId(String clientId, String workerId) {
        // Create a consistent chat ID for the client-worker pair
        return clientId.compareTo(workerId) < 0 
                ? clientId + "_" + workerId 
                : workerId + "_" + clientId;
    }
} 