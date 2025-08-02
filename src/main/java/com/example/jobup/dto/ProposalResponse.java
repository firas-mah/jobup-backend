package com.example.jobup.dto;

import com.example.jobup.entities.ProposalStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProposalResponse {
    private String id;
    private String clientId;
    private String workerId;
    private String title;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private String location;
    private LocalDateTime scheduledTime;
    private ProposalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String sentBy;
    private String acceptedBy;
    private LocalDateTime acceptedAt;
    private String declinedBy;
    private LocalDateTime declinedAt;
    private String completedBy;
    private LocalDateTime completedAt;
    private Integer rating;
    private String feedback;
    private LocalDateTime ratedAt;
}