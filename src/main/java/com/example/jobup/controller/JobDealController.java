package com.example.jobup.controller;

import com.example.jobup.dto.JobDealDto;
import com.example.jobup.entities.JobDeal;
import com.example.jobup.services.JobDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class JobDealController {
    
    private final JobDealService dealService;
    
    @PostMapping("/from-proposal/{proposalId}")
    public ResponseEntity<JobDealDto> createDealFromProposal(@PathVariable String proposalId) {
        JobDealDto deal = dealService.createDealFromProposal(proposalId);
        return ResponseEntity.ok(deal);
    }
    
    @PutMapping("/{dealId}/status")
    public ResponseEntity<JobDealDto> updateDealStatus(
            @PathVariable String dealId,
            @RequestBody UpdateStatusRequest request) {
        
        JobDealDto deal = dealService.updateDealStatus(dealId, request.getStatus());
        return ResponseEntity.ok(deal);
    }
    
    @PostMapping("/{dealId}/rating")
    public ResponseEntity<JobDealDto> addRating(
            @PathVariable String dealId,
            @RequestBody RatingRequest request) {
        
        JobDealDto deal = dealService.addRating(dealId, request.getRating(), request.getReview());
        return ResponseEntity.ok(deal);
    }
    
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<JobDealDto>> getDealsByChatId(@PathVariable String chatId) {
        List<JobDealDto> deals = dealService.getDealsByChatId(chatId);
        return ResponseEntity.ok(deals);
    }
    
    public static class UpdateStatusRequest {
        private JobDeal.DealStatus status;
        
        public JobDeal.DealStatus getStatus() { return status; }
        public void setStatus(JobDeal.DealStatus status) { this.status = status; }
    }
    
    public static class RatingRequest {
        private Integer rating;
        private String review;
        
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getReview() { return review; }
        public void setReview(String review) { this.review = review; }
    }
} 