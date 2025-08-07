package com.example.jobup.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    private String id;
    private String chatId;
    
    // Sender information
    private String senderId;
    private String senderName;
    private String senderType; // CLIENT or WORKER
    
    // Receiver information (NEW FIELDS)
    private String receiverId;
    private String receiverName;
    private String receiverType; // CLIENT or WORKER
    
    private String content;
    private LocalDateTime timestamp;
    private MessageType messageType;
    private String proposalId;
    
    public enum MessageType {
        TEXT,
        PROPOSAL,
        PROPOSAL_RESPONSE,
        JOB_CONFIRMED,
        JOB_COMPLETED,
        RATING
    }
}