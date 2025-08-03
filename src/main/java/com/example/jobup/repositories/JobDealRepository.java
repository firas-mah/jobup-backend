package com.example.jobup.repositories;

import com.example.jobup.entities.JobDeal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobDealRepository extends MongoRepository<JobDeal, String> {
    List<JobDeal> findByChatId(String chatId);
    List<JobDeal> findByClientId(String clientId);
    List<JobDeal> findByWorkerId(String workerId);
} 