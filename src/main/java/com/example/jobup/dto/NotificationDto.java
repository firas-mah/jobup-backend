package com.example.jobup.dto;

import com.example.jobup.entities.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String id;
    private String recipientId;
    private String recipientName;
    private String senderId;
    private String senderName;
    private String postId;
    private String postTitle;
    private Notification.NotificationType type;
    private String message;
    private String actionUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
}
