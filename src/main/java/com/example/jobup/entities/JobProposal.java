package com.example.jobup.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "job_proposals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobProposal {
    @Id
    private String id;
    private String chatId;
    private String senderId;
    private String senderName;
    private String senderType;
    private String title;
    private String description;
    private Integer duration; // in hours
    private BigDecimal price;
    private String location;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private ProposalStatus status;
    
    public enum ProposalStatus {
        PENDING,
        ACCEPTED,
        DECLINED,
        NEGOTIATED
    }
} 