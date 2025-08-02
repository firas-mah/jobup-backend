package com.example.jobup.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    private String id;

    @Indexed
    private String chatId; // Combination of clientId-workerId

    private String senderId;
    private String senderName;
    private String senderType; // "CLIENT" or "WORKER"
    private String content;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private MessageType messageType; // TEXT, PROPOSAL, PROPOSAL_RESPONSE
    private String proposalId; // Reference to proposal if messageType is PROPOSAL
} 