package com.example.jobup.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String senderName;
    private String senderType;
    private String content;
} 