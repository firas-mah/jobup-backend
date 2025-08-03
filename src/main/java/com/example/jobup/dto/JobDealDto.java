package com.example.jobup.dto;

import com.example.jobup.entities.JobDeal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDealDto {
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
    private JobDeal.DealStatus status;
    private Integer rating;
    private String review;
    private LocalDateTime completedAt;
} 