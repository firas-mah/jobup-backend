package com.example.jobup.controller;

import com.example.jobup.dto.ChatMessageDto;
import com.example.jobup.dto.WebSocketMessage;
import com.example.jobup.entities.ChatMessage;
import com.example.jobup.entities.MessageType;
import com.example.jobup.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessageDto sendMessage(@Payload WebSocketMessage webSocketMessage, 
                                   SimpMessageHeaderAccessor headerAccessor) {
        
        // Save message to database
        ChatMessage savedMessage = chatMessageService.sendTextMessage(
            webSocketMessage.getChatId(),
            webSocketMessage.getSenderId(),
            webSocketMessage.getSenderName(),
            webSocketMessage.getSenderType(),
            webSocketMessage.getContent()
        );

        // Convert to DTO and broadcast
        ChatMessageDto messageDto = new ChatMessageDto();
        messageDto.setId(savedMessage.getId());
        messageDto.setChatId(savedMessage.getChatId());
        messageDto.setSenderId(savedMessage.getSenderId());
        messageDto.setSenderName(savedMessage.getSenderName());
        messageDto.setSenderType(savedMessage.getSenderType());
        messageDto.setContent(savedMessage.getContent());
        messageDto.setTimestamp(savedMessage.getTimestamp());
        messageDto.setMessageType(savedMessage.getMessageType().name());
        messageDto.setProposalId(savedMessage.getProposalId());

        return messageDto;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessageDto addUser(@Payload WebSocketMessage webSocketMessage, 
                                SimpMessageHeaderAccessor headerAccessor) {
        
        // Add username to web socket session
        headerAccessor.getSessionAttributes().put("username", webSocketMessage.getSenderName());
        headerAccessor.getSessionAttributes().put("chatId", webSocketMessage.getChatId());
        
        // Send join notification
        ChatMessageDto joinMessage = new ChatMessageDto();
        joinMessage.setMessageType("JOIN");
        joinMessage.setSenderName(webSocketMessage.getSenderName());
        joinMessage.setContent(webSocketMessage.getSenderName() + " joined the chat");
        joinMessage.setTimestamp(LocalDateTime.now());
        
        return joinMessage;
    }
} 