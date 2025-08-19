package com.example.jobup.services;

import com.example.jobup.dto.WorkerRatingStatsDto;
import com.example.jobup.entities.JobDeal;
import com.example.jobup.entities.Worker;
import com.example.jobup.repositories.JobDealRepository;
import com.example.jobup.repositories.WorkerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerRatingService {
    
    private final JobDealRepository dealRepository;
    private final WorkerRepo workerRepository;
    
    /**
     * Calculate and update worker's average rating based on completed deals
     */
    public void updateWorkerRating(String workerId) {
        List<JobDeal> ratedDeals = dealRepository.findCompletedDealsWithRatingsByWorkerId(workerId);
        
        if (ratedDeals.isEmpty()) {
            log.info("No rated deals found for worker: {}", workerId);
            return;
        }
        
        OptionalDouble averageRating = ratedDeals.stream()
                .mapToInt(JobDeal::getRating)
                .average();
        
        if (averageRating.isPresent()) {
            Worker worker = workerRepository.findById(workerId)
                    .orElseThrow(() -> new RuntimeException("Worker not found: " + workerId));
            
            double newRating = Math.round(averageRating.getAsDouble() * 100.0) / 100.0; // Round to 2 decimal places
            worker.setRating(newRating);
            workerRepository.save(worker);
            
            log.info("Updated rating for worker {}: {} (based on {} ratings)", 
                    workerId, newRating, ratedDeals.size());
        }
    }
    
    /**
     * Get comprehensive rating statistics for a worker
     */
    public WorkerRatingStatsDto getWorkerRatingStats(String workerId) {
        List<JobDeal> completedDeals = dealRepository.findByWorkerIdAndStatus(workerId, JobDeal.DealStatus.COMPLETED);
        List<JobDeal> ratedDeals = dealRepository.findCompletedDealsWithRatingsByWorkerId(workerId);
        
        if (ratedDeals.isEmpty()) {
            return WorkerRatingStatsDto.builder()
                    .workerId(workerId)
                    .averageRating(0.0)
                    .totalRatings(0)
                    .totalCompletedJobs(completedDeals.size())
                    .ratingDistribution(WorkerRatingStatsDto.RatingDistribution.builder()
                            .fiveStars(0)
                            .fourStars(0)
                            .threeStars(0)
                            .twoStars(0)
                            .oneStar(0)
                            .build())
                    .build();
        }
        
        // Calculate average rating
        double averageRating = ratedDeals.stream()
                .mapToInt(JobDeal::getRating)
                .average()
                .orElse(0.0);
        
        // Calculate rating distribution
        WorkerRatingStatsDto.RatingDistribution distribution = calculateRatingDistribution(ratedDeals);
        
        return WorkerRatingStatsDto.builder()
                .workerId(workerId)
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .totalRatings(ratedDeals.size())
                .totalCompletedJobs(completedDeals.size())
                .ratingDistribution(distribution)
                .build();
    }
    
    private WorkerRatingStatsDto.RatingDistribution calculateRatingDistribution(List<JobDeal> ratedDeals) {
        int[] counts = new int[6]; // Index 0 unused, 1-5 for star ratings
        
        for (JobDeal deal : ratedDeals) {
            if (deal.getRating() != null && deal.getRating() >= 1 && deal.getRating() <= 5) {
                counts[deal.getRating()]++;
            }
        }
        
        return WorkerRatingStatsDto.RatingDistribution.builder()
                .oneStar(counts[1])
                .twoStars(counts[2])
                .threeStars(counts[3])
                .fourStars(counts[4])
                .fiveStars(counts[5])
                .build();
    }
    
    /**
     * Update all worker ratings (useful for batch operations)
     */
    public void updateAllWorkerRatings() {
        List<Worker> workers = workerRepository.findAll();
        log.info("Starting batch update of ratings for {} workers", workers.size());
        
        for (Worker worker : workers) {
            try {
                updateWorkerRating(worker.getId());
            } catch (Exception e) {
                log.error("Failed to update rating for worker {}: {}", worker.getId(), e.getMessage());
            }
        }
        
        log.info("Completed batch update of worker ratings");
    }
}
