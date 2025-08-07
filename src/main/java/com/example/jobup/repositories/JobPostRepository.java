package com.example.jobup.repositories;

import com.example.jobup.entities.JobPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostRepository extends MongoRepository<JobPost, String> {
    // Add custom queries if needed
}