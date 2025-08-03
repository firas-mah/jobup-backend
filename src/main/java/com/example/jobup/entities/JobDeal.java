package com.example.jobup.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "job_deals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDeal {
    @Id
    private String id;
    private String proposalId;
    private String chatId;
    private String clientId;
    private String workerId;
    private String title;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private String location;
    private LocalDateTime scheduledTime;
    private LocalDateTime confirmedAt;
    private DealStatus status;
    private Integer rating;
    private String review;
    private LocalDateTime completedAt;
    
    public enum DealStatus {
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
} 