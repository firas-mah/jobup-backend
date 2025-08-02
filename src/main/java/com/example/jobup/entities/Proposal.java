package com.example.jobup.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "proposals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {
    @Id
    private String id;

    @Indexed
    private String clientId;

    @Indexed
    private String workerId;

    private String title;
    private String description;
    private Integer duration; // in hours
    private BigDecimal price; // in TND
    private String location;
    private LocalDateTime scheduledTime;
    
    @Builder.Default
    private ProposalStatus status = ProposalStatus.PENDING;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    private String sentBy; // "CLIENT" or "WORKER"
    
    // Additional fields for tracking
    private String acceptedBy;
    private LocalDateTime acceptedAt;
    private String declinedBy;
    private LocalDateTime declinedAt;
    private String completedBy;
    private LocalDateTime completedAt;
    
    // Rating and feedback
    private Integer rating;
    private String feedback;
    private LocalDateTime ratedAt;
} 