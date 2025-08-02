package com.example.jobup.dto;

import lombok.Data;

@Data
public class WebSocketMessage {
    private String type; // "TEXT", "PROPOSAL", "PROPOSAL_RESPONSE"
    private String chatId;
    private String senderId;
    private String senderName;
    private String senderType;
    private String content;
    private String proposalId;
} 