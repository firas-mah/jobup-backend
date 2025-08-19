package com.example.jobup.services;

import com.example.jobup.dto.JobDealDto;
import com.example.jobup.entities.JobDeal;
import com.example.jobup.entities.JobProposal;
import com.example.jobup.repositories.JobDealRepository;
import com.example.jobup.repositories.JobProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobDealService {

    private final JobDealRepository dealRepository;
    private final JobProposalRepository proposalRepository;
    private final WorkerRatingService workerRatingService;
    
    public JobDealDto createDealFromProposal(String proposalId) {
        JobProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        // Determine client and worker IDs based on sender type
        String clientId = proposal.getSenderType().equals("ROLE_CLIENT") ? proposal.getSenderId() : null;
        String workerId = proposal.getSenderType().equals("ROLE_WORKER") ? proposal.getSenderId() : null;
        
        JobDeal deal = JobDeal.builder()
                .proposalId(proposalId)
                .chatId(proposal.getChatId())
                .clientId(clientId)
                .workerId(workerId)
                .title(proposal.getTitle())
                .description(proposal.getDescription())
                .duration(proposal.getDuration())
                .price(proposal.getPrice())
                .location(proposal.getLocation())
                .scheduledTime(proposal.getScheduledTime())
                .confirmedAt(LocalDateTime.now())
                .status(JobDeal.DealStatus.CONFIRMED)
                .build();
        
        JobDeal savedDeal = dealRepository.save(deal);
        return convertToDto(savedDeal);
    }
    
    public JobDealDto updateDealStatus(String dealId, JobDeal.DealStatus status) {
        JobDeal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new RuntimeException("Deal not found"));
        
        deal.setStatus(status);
        if (status == JobDeal.DealStatus.COMPLETED) {
            deal.setCompletedAt(LocalDateTime.now());
        }
        
        JobDeal savedDeal = dealRepository.save(deal);
        return convertToDto(savedDeal);
    }
    
    public JobDealDto addRating(String dealId, Integer rating, String review) {
        // Validate rating range
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        JobDeal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new RuntimeException("Deal not found"));

        // Validate deal status - only completed deals can be rated
        if (deal.getStatus() != JobDeal.DealStatus.COMPLETED) {
            throw new IllegalStateException("Only completed deals can be rated");
        }

        // Prevent duplicate ratings
        if (deal.getRating() != null) {
            throw new IllegalStateException("This deal has already been rated");
        }

        // Validate review length if provided
        if (review != null && review.length() > 1000) {
            throw new IllegalArgumentException("Review cannot exceed 1000 characters");
        }

        deal.setRating(rating);
        deal.setReview(review);

        JobDeal savedDeal = dealRepository.save(deal);

        // Update worker's average rating
        if (savedDeal.getWorkerId() != null) {
            try {
                workerRatingService.updateWorkerRating(savedDeal.getWorkerId());
            } catch (Exception e) {
                // Log error but don't fail the rating submission
                System.err.println("Failed to update worker rating: " + e.getMessage());
            }
        }

        return convertToDto(savedDeal);
    }
    
    public List<JobDealDto> getDealsByChatId(String chatId) {
        List<JobDeal> deals = dealRepository.findByChatId(chatId);
        return deals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<JobDealDto> getDealsByWorkerId(String workerId) {
        List<JobDeal> deals = dealRepository.findByWorkerId(workerId);
        return deals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<JobDealDto> getCompletedDealsByWorkerId(String workerId) {
        List<JobDeal> deals = dealRepository.findByWorkerIdAndStatus(workerId, JobDeal.DealStatus.COMPLETED);
        return deals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<JobDealDto> getRatedDealsByWorkerId(String workerId) {
        List<JobDeal> deals = dealRepository.findCompletedDealsWithRatingsByWorkerId(workerId);
        return deals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public boolean canRateDeal(String dealId) {
        JobDeal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new RuntimeException("Deal not found"));

        return deal.getStatus() == JobDeal.DealStatus.COMPLETED && deal.getRating() == null;
    }
    
    private JobDealDto convertToDto(JobDeal deal) {
        return JobDealDto.builder()
                .id(deal.getId())
                .proposalId(deal.getProposalId())
                .chatId(deal.getChatId())
                .clientId(deal.getClientId())
                .workerId(deal.getWorkerId())
                .title(deal.getTitle())
                .description(deal.getDescription())
                .duration(deal.getDuration())
                .price(deal.getPrice())
                .location(deal.getLocation())
                .scheduledTime(deal.getScheduledTime())
                .confirmedAt(deal.getConfirmedAt())
                .status(deal.getStatus())
                .rating(deal.getRating())
                .review(deal.getReview())
                .completedAt(deal.getCompletedAt())
                .build();
    }
} 