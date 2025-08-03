package com.example.jobup.controller;

import com.example.jobup.dto.JobProposalDto;
import com.example.jobup.entities.JobProposal;
import com.example.jobup.services.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalController {
    
    private final ProposalService proposalService;
    
    @PostMapping
    public ResponseEntity<JobProposalDto> createProposal(@RequestBody CreateProposalRequest request) {
        JobProposalDto proposal = proposalService.createProposal(
                request.getChatId(),
                request.getSenderId(),
                request.getSenderName(),
                request.getSenderType(),
                request.getTitle(),
                request.getDescription(),
                request.getDuration(),
                request.getPrice(),
                request.getLocation(),
                request.getScheduledTime()
        );
        
        return ResponseEntity.ok(proposal);
    }
    
    @PutMapping("/{proposalId}/status")
    public ResponseEntity<JobProposalDto> updateProposalStatus(
            @PathVariable String proposalId,
            @RequestBody UpdateStatusRequest request) {
        
        JobProposalDto proposal = proposalService.updateProposalStatus(proposalId, request.getStatus());
        return ResponseEntity.ok(proposal);
    }
    
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<JobProposalDto>> getProposalsByChatId(@PathVariable String chatId) {
        List<JobProposalDto> proposals = proposalService.getProposalsByChatId(chatId);
        return ResponseEntity.ok(proposals);
    }
    
    public static class CreateProposalRequest {
        private String chatId;
        private String senderId;
        private String senderName;
        private String senderType;
        private String title;
        private String description;
        private Integer duration;
        private java.math.BigDecimal price;
        private String location;
        private LocalDateTime scheduledTime;
        
        // Getters and setters
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        public String getSenderType() { return senderType; }
        public void setSenderType(String senderType) { this.senderType = senderType; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public java.math.BigDecimal getPrice() { return price; }
        public void setPrice(java.math.BigDecimal price) { this.price = price; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    }
    
    public static class UpdateStatusRequest {
        private JobProposal.ProposalStatus status;
        
        public JobProposal.ProposalStatus getStatus() { return status; }
        public void setStatus(JobProposal.ProposalStatus status) { this.status = status; }
    }
} 