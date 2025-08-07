package com.example.jobup.controller;

import com.example.jobup.dto.ChatMessageDto;
import com.example.jobup.dto.JobProposalDto;
import com.example.jobup.entities.ChatMessage;
import com.example.jobup.services.ChatService;
import com.example.jobup.services.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    
    private final ChatService chatService;
    private final ProposalService proposalService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/chat.sendMessage")
    public ChatMessageDto sendMessage(@Payload WebSocketMessage webSocketMessage, 
                                   SimpMessageHeaderAccessor headerAccessor) {
        
        String chatId = webSocketMessage.getChatId();
        
        // Determine receiver information based on sender
        String receiverId = webSocketMessage.getReceiverId();
        String receiverName = webSocketMessage.getReceiverName();
        String receiverType = webSocketMessage.getReceiverType();
        
        ChatMessageDto message = chatService.sendMessage(
            chatId,
            webSocketMessage.getSenderId(),
            webSocketMessage.getSenderName(),
            webSocketMessage.getSenderType(),
            receiverId,
            receiverName,
            receiverType,
            webSocketMessage.getContent(),
            ChatMessage.MessageType.TEXT
        );
        
        // Send to specific chat topic
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);
        return message;
    }
    
    @MessageMapping("/chat.sendProposal")
    public JobProposalDto sendProposal(@Payload WebSocketProposalMessage proposalMessage, 
                                     SimpMessageHeaderAccessor headerAccessor) {
        
        String chatId = proposalMessage.getChatId();
        
        // Determine receiver information based on sender
        String receiverId = proposalMessage.getReceiverId();
        String receiverName = proposalMessage.getReceiverName();
        String receiverType = proposalMessage.getReceiverType();
        
        // Create proposal
        JobProposalDto proposal = proposalService.createProposal(
            chatId,
            proposalMessage.getSenderId(),
            proposalMessage.getSenderName(),
            proposalMessage.getSenderType(),
            receiverId,
            receiverName,
            receiverType,
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
            receiverId,
            receiverName,
            receiverType,
            "Sent a proposal: " + proposalMessage.getTitle(),
            ChatMessage.MessageType.PROPOSAL
        );

        // Send to specific chat topic
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, proposal);
        return proposal;
    }

    @MessageMapping("/chat.addUser")
    public ChatMessageDto addUser(@Payload WebSocketMessage webSocketMessage, 
                                SimpMessageHeaderAccessor headerAccessor) {
        
        String chatId = webSocketMessage.getChatId();
        
        // Add username to web socket session
        headerAccessor.getSessionAttributes().put("username", webSocketMessage.getSenderName());
        headerAccessor.getSessionAttributes().put("chatId", webSocketMessage.getChatId());
        
        // Determine receiver information
        String receiverId = webSocketMessage.getReceiverId();
        String receiverName = webSocketMessage.getReceiverName();
        String receiverType = webSocketMessage.getReceiverType();
        
        // Send join notification
        ChatMessageDto joinMessage = chatService.sendMessage(
            chatId,
            webSocketMessage.getSenderId(),
            webSocketMessage.getSenderName(),
            webSocketMessage.getSenderType(),
            receiverId,
            receiverName,
            receiverType,
            webSocketMessage.getSenderName() + " joined the chat",
            ChatMessage.MessageType.TEXT
        );
        
        // Send to specific chat topic
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, joinMessage);
        return joinMessage;
    }
    
    public static class WebSocketMessage {
        private String chatId;
        private String senderId;
        private String senderName;
        private String senderType;
        private String receiverId;
        private String receiverName;
        private String receiverType;
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
        public String getReceiverId() { return receiverId; }
        public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverType() { return receiverType; }
        public void setReceiverType(String receiverType) { this.receiverType = receiverType; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class WebSocketProposalMessage {
        private String chatId;
        private String senderId;
        private String senderName;
        private String senderType;
        private String receiverId;
        private String receiverName;
        private String receiverType;
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
        public String getReceiverId() { return receiverId; }
        public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverType() { return receiverType; }
        public void setReceiverType(String receiverType) { this.receiverType = receiverType; }
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