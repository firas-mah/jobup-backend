package com.example.jobup.repositories;

import com.example.jobup.entities.Worker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepo extends MongoRepository<Worker, String> {
    List<Worker> findByLocationIgnoreCase(String location);
    List<Worker> findByJobTypeIgnoreCase(String jobType);
    }
