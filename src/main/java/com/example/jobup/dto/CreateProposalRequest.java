package com.example.jobup.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateProposalRequest {
    private String workerId;
    private String title;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private String location;
    private LocalDateTime scheduledTime;
}
