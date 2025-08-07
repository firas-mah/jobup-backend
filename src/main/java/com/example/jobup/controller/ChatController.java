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
@CrossOrigin(origins = "*")
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
                request.getReceiverId(),
                request.getReceiverName(),
                request.getReceiverType(),
                request.getContent(),
                ChatMessage.MessageType.TEXT
        );
        
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<ChatMessageDto>> getMessagesByReceiverId(@PathVariable String receiverId) {
        List<ChatMessageDto> messages = chatService.getMessagesByReceiverId(receiverId);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/receiver/{receiverId}/type/{receiverType}")
    public ResponseEntity<List<ChatMessageDto>> getMessagesByReceiverIdAndType(
            @PathVariable String receiverId,
            @PathVariable String receiverType) {
        List<ChatMessageDto> messages = chatService.getMessagesByReceiverIdAndType(receiverId, receiverType);
        return ResponseEntity.ok(messages);
    }
    
    public static class SendMessageRequest {
        private String senderId;
        private String senderName;
        private String senderType;
        private String receiverId;
        private String receiverName;
        private String receiverType;
        private String content;
        
        // Getters and setters
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        public String getSenderType() { return senderType; }
        public void setSenderType(String senderType) { this.senderType = senderType; }
        public String getReceiverId() { return receiverId; }
        public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverType() { return receiverType; }
        public void setReceiverType(String receiverType) { this.receiverType = receiverType; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
} 