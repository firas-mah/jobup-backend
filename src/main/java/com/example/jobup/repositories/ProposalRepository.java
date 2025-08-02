package com.example.jobup.repositories;

import com.example.jobup.entities.Proposal;
import com.example.jobup.entities.ProposalStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalRepository extends MongoRepository<Proposal, String> {
    
    // Find proposals where user is the worker (recipient)
    List<Proposal> findByWorkerIdOrderByCreatedAtDesc(String workerId);
    
    // Find proposals where user is the client (sender)
    List<Proposal> findByClientIdOrderByCreatedAtDesc(String clientId);
    
    // Find proposals where user is either client or worker
    @Query("{'$or': [{'clientId': ?0}, {'workerId': ?0}], 'status': {'$in': ['PENDING', 'NEGOTIATING']}}")
    List<Proposal> findActiveProposalsForUser(String userId);

    // Find proposals between specific client and worker
    @Query("{'$or': [{'clientId': ?0, 'workerId': ?1}, {'clientId': ?1, 'workerId': ?0}]}")
    List<Proposal> findByClientIdAndWorkerIdOrderByCreatedAtDesc(String clientId, String workerId);
    
    // Find proposals by status
    List<Proposal> findByStatus(ProposalStatus status);
    
    // Find pending proposals for a worker
    List<Proposal> findByWorkerIdAndStatusOrderByCreatedAtDesc(String workerId, ProposalStatus status);
    
    // Find pending proposals for a client
    List<Proposal> findByClientIdAndStatusOrderByCreatedAtDesc(String clientId, ProposalStatus status);
    
    // Find proposals that need attention (pending or negotiating)
    // This query is now redundant as findActiveProposalsForUser handles this
    // @Query("{'$or': [{'clientId': ?0}, {'workerId': ?0}], 'status': {'$in': ['PENDING', 'NEGOTIATING']}}")
    // List<Proposal> findActiveProposalsForUser(String userId);
} 