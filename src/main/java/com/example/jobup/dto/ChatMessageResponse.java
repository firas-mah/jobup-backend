package com.example.jobup.dto;

import com.example.jobup.entities.MessageType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private String id;
    private String chatId;
    private String senderId;
    private String senderName;
    private String senderType;
    private String content;
    private LocalDateTime timestamp;
    private MessageType messageType;
    private String proposalId;
    private ProposalResponse proposal; // Include proposal details if messageType is PROPOSAL
} 