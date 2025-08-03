package com.example.jobup.controller;

import com.example.jobup.dto.ChatMessageDto;
import com.example.jobup.entities.ChatMessage;
import com.example.jobup.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getChatMessages(@PathVariable String chatId) {
        List<ChatMessageDto> messages = chatService.getChatMessages(chatId);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable String chatId,
            @RequestBody SendMessageRequest request) {
        
        ChatMessageDto message = chatService.sendMessage(
                chatId,
                request.getSenderId(),
                request.getSenderName(),
                request.getSenderType(),
                request.getContent(),
                ChatMessage.MessageType.TEXT
        );
        
        return ResponseEntity.ok(message);
    }
    
    public static class SendMessageRequest {
        private String senderId;
        private String senderName;
        private String senderType;
        private String content;
        
        // Getters and setters
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        public String getSenderType() { return senderType; }
        public void setSenderType(String senderType) { this.senderType = senderType; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
} 