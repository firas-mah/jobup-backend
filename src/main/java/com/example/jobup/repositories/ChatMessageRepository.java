package com.example.jobup.repositories;

import com.example.jobup.entities.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    // Find messages by chat ID (client-worker combination)
    List<ChatMessage> findByChatIdOrderByTimestampAsc(String chatId);
    
    // Find recent messages for a chat
    List<ChatMessage> findByChatIdOrderByTimestampDesc(String chatId);
} 