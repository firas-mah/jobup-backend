package com.example.jobup.controller;

import com.example.jobup.dto.SendMessageRequest;
import com.example.jobup.entities.ChatMessage;
import com.example.jobup.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController {
    
    private final ChatMessageService chatMessageService;
    
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ChatMessage> sendMessage(
            @PathVariable String chatId,
            @RequestBody SendMessageRequest request,
            Authentication authentication) {
        
        String senderId = authentication.getName();
        String senderName = request.getSenderName();
        String senderType = request.getSenderType();
        String content = request.getContent();
        
        ChatMessage message = chatMessageService.sendTextMessage(chatId, senderId, senderName, senderType, content);
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable String chatId) {
        List<ChatMessage> messages = chatMessageService.getChatMessages(chatId);
        return ResponseEntity.ok(messages);
    }
} 