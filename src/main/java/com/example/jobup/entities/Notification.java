package com.example.jobup.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    private String id;
    
    private String recipientId;
    private String recipientName;
    private String senderId;
    private String senderName;
    private String postId;
    private String postTitle;
    
    private NotificationType type;
    private String message;
    private String actionUrl;
    
    @Builder.Default
    private boolean isRead = false;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum NotificationType {
        POST_LIKED,
        POST_COMMENTED,
        POST_SAVED,
        PROPOSAL_RECEIVED,
        PROPOSAL_ACCEPTED,
        PROPOSAL_DECLINED
    }
}
