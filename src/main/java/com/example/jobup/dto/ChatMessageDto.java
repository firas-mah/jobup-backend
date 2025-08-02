package com.example.jobup.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private String id;
    private String chatId;
    private String senderId;
    private String senderName;
    private String senderType;
    private String content;
    private LocalDateTime timestamp;
    private String messageType;
    private String proposalId;
} 