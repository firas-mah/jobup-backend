package com.example.jobup.repositories;

import com.example.jobup.entities.JobDeal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobDealRepository extends MongoRepository<JobDeal, String> {
    List<JobDeal> findByChatId(String chatId);
    List<JobDeal> findByClientId(String clientId);
    List<JobDeal> findByWorkerId(String workerId);

    // Rating-related queries
    List<JobDeal> findByWorkerIdAndStatus(String workerId, JobDeal.DealStatus status);
    List<JobDeal> findByWorkerIdAndStatusAndRatingIsNotNull(String workerId, JobDeal.DealStatus status);

    @Query("{ 'workerId': ?0, 'status': 'COMPLETED', 'rating': { $ne: null } }")
    List<JobDeal> findCompletedDealsWithRatingsByWorkerId(String workerId);

    @Query("{ 'workerId': ?0, 'status': 'COMPLETED', 'rating': null }")
    List<JobDeal> findCompletedDealsWithoutRatingsByWorkerId(String workerId);

    // Check if a specific deal already has a rating
    Optional<JobDeal> findByIdAndRatingIsNotNull(String dealId);
}