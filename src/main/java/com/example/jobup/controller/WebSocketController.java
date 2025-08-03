package com.example.jobup.controller;

import com.example.jobup.dto.ChatMessageDto;
import com.example.jobup.dto.JobProposalDto;
import com.example.jobup.entities.ChatMessage;
import com.example.jobup.entities.JobProposal;
import com.example.jobup.services.ChatService;
import com.example.jobup.services.ProposalService;
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

    private final ChatService chatService;
    private final ProposalService proposalService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessageDto sendMessage(@Payload WebSocketMessage webSocketMessage, 
                                   SimpMessageHeaderAccessor headerAccessor) {
        
        String chatId = webSocketMessage.getChatId();
        
        // Save message to database
        ChatMessageDto savedMessage = chatService.sendMessage(
            chatId,
            webSocketMessage.getSenderId(),
            webSocketMessage.getSenderName(),
            webSocketMessage.getSenderType(),
            webSocketMessage.getContent(),
            ChatMessage.MessageType.TEXT
        );

        return savedMessage;
    }

    @MessageMapping("/chat.sendProposal")
    @SendTo("/topic/chat/{chatId}")
    public JobProposalDto sendProposal(@Payload WebSocketProposalMessage proposalMessage, 
                                     SimpMessageHeaderAccessor headerAccessor) {
        
        String chatId = proposalMessage.getChatId();
        
        // Create proposal
        JobProposalDto proposal = proposalService.createProposal(
            chatId,
            proposalMessage.getSenderId(),
            proposalMessage.getSenderName(),
            proposalMessage.getSenderType(),
            proposalMessage.getTitle(),
            proposalMessage.getDescription(),
            proposalMessage.getDuration(),
            proposalMessage.getPrice(),
            proposalMessage.getLocation(),
            proposalMessage.getScheduledTime()
        );
        
        // Also send a chat message about the proposal
        chatService.sendMessage(
            chatId,
            proposalMessage.getSenderId(),
            proposalMessage.getSenderName(),
            proposalMessage.getSenderType(),
            "Sent a proposal: " + proposalMessage.getTitle(),
            ChatMessage.MessageType.PROPOSAL
        );

        return proposal;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessageDto addUser(@Payload WebSocketMessage webSocketMessage, 
                                SimpMessageHeaderAccessor headerAccessor) {
        
        String chatId = webSocketMessage.getChatId();
        
        // Add username to web socket session
        headerAccessor.getSessionAttributes().put("username", webSocketMessage.getSenderName());
        headerAccessor.getSessionAttributes().put("chatId", webSocketMessage.getChatId());
        
        // Send join notification
        ChatMessageDto joinMessage = chatService.sendMessage(
            chatId,
            webSocketMessage.getSenderId(),
            webSocketMessage.getSenderName(),
            webSocketMessage.getSenderType(),
            webSocketMessage.getSenderName() + " joined the chat",
            ChatMessage.MessageType.TEXT
        );
        
        return joinMessage;
    }
    
    public static class WebSocketMessage {
        private String chatId;
        private String senderId;
        private String senderName;
        private String senderType;
        private String content;
        
        // Getters and setters
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        public String getSenderType() { return senderType; }
        public void setSenderType(String senderType) { this.senderType = senderType; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class WebSocketProposalMessage {
        private String chatId;
        private String senderId;
        private String senderName;
        private String senderType;
        private String title;
        private String description;
        private Integer duration;
        private java.math.BigDecimal price;
        private String location;
        private LocalDateTime scheduledTime;
        
        // Getters and setters
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        public String getSenderType() { return senderType; }
        public void setSenderType(String senderType) { this.senderType = senderType; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public java.math.BigDecimal getPrice() { return price; }
        public void setPrice(java.math.BigDecimal price) { this.price = price; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    }
} 