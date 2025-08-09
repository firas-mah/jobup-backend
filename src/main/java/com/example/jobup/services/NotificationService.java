package com.example.jobup.services;

import com.example.jobup.dto.NotificationDto;
import com.example.jobup.entities.Notification;
import com.example.jobup.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    public NotificationDto createNotification(
            String recipientId, 
            String recipientName,
            String senderId, 
            String senderName,
            String postId,
            String postTitle,
            Notification.NotificationType type,
            String customMessage) {
        
        // Create notification message based on type
        String message = customMessage != null ? customMessage : generateMessage(type, senderName, postTitle);
        String actionUrl = generateActionUrl(type, postId);
        
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .recipientName(recipientName)
                .senderId(senderId)
                .senderName(senderName)
                .postId(postId)
                .postTitle(postTitle)
                .type(type)
                .message(message)
                .actionUrl(actionUrl)
                .build();
        
        Notification saved = notificationRepository.save(notification);
        NotificationDto dto = toDto(saved);
        
        // Send real-time notification via WebSocket
        sendRealTimeNotification(recipientId, dto);
        
        return dto;
    }
    
    public List<NotificationDto> getNotificationsByUserId(String userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<NotificationDto> getUnreadNotificationsByUserId(String userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public long getUnreadNotificationCount(String userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }
    
    public NotificationDto markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        
        return toDto(updated);
    }
    
    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }
    
    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    private void sendRealTimeNotification(String recipientId, NotificationDto notification) {
        try {
            // Send to specific user's notification channel
            messagingTemplate.convertAndSendToUser(
                recipientId, 
                "/queue/notifications", 
                notification
            );
            
            // Also send notification count update
            long unreadCount = getUnreadNotificationCount(recipientId);
            messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/notification-count",
                unreadCount
            );
        } catch (Exception e) {
            System.err.println("Failed to send real-time notification: " + e.getMessage());
        }
    }
    
    private String generateMessage(Notification.NotificationType type, String senderName, String postTitle) {
        switch (type) {
            case POST_LIKED:
                return senderName + " liked your post: " + postTitle;
            case POST_COMMENTED:
                return senderName + " commented on your post: " + postTitle;
            case POST_SAVED:
                return senderName + " saved your post: " + postTitle;
            case PROPOSAL_RECEIVED:
                return senderName + " sent you a proposal for: " + postTitle;
            case PROPOSAL_ACCEPTED:
                return senderName + " accepted your proposal for: " + postTitle;
            case PROPOSAL_DECLINED:
                return senderName + " declined your proposal for: " + postTitle;
            default:
                return "You have a new notification";
        }
    }
    
    private String generateActionUrl(Notification.NotificationType type, String postId) {
        switch (type) {
            case POST_LIKED:
            case POST_COMMENTED:
            case POST_SAVED:
                return "/client/my-posts"; // Redirect to my posts to see the activity
            case PROPOSAL_RECEIVED:
            case PROPOSAL_ACCEPTED:
            case PROPOSAL_DECLINED:
                return "/client/proposals"; // Redirect to proposals page
            default:
                return "/client/home";
        }
    }
    
    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipientId())
                .recipientName(notification.getRecipientName())
                .senderId(notification.getSenderId())
                .senderName(notification.getSenderName())
                .postId(notification.getPostId())
                .postTitle(notification.getPostTitle())
                .type(notification.getType())
                .message(notification.getMessage())
                .actionUrl(notification.getActionUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
