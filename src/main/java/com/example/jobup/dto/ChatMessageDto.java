package com.example.jobup.dto;

import com.example.jobup.entities.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String id;
    private String chatId;
    private String senderId;
    private String senderName;
    private String senderType;
    private String content;
    private LocalDateTime timestamp;
    private ChatMessage.MessageType messageType;
    private String proposalId;
}
