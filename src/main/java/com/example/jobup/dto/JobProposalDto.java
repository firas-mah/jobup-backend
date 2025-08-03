package com.example.jobup.dto;

import com.example.jobup.entities.JobProposal;
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
public class JobProposalDto {
    private String id;
    private String chatId;
    private String senderId;
    private String senderName;
    private String senderType;
    private String title;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private String location;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private JobProposal.ProposalStatus status;
}