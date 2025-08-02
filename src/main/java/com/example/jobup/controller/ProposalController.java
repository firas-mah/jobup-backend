package com.example.jobup.controller;

import com.example.jobup.dto.CreateProposalRequest;
import com.example.jobup.dto.ProposalResponse;
import com.example.jobup.entities.Role;
import com.example.jobup.entities.User;
import com.example.jobup.repositories.UserRepository;
import com.example.jobup.services.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.mongodb.core.query.Query;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalController {
    
    private final ProposalService proposalService;
    private final UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<ProposalResponse> createProposal(
            @RequestBody CreateProposalRequest request,
            Authentication authentication) {
        
        String userId = authentication.getName();
        // Determine user type based on authentication
        String userType = "CLIENT"; // This should be determined from user roles
        
        ProposalResponse response = proposalService.createProposal(request, userId, userType);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{proposalId}/accept")
    public ResponseEntity<ProposalResponse> acceptProposal(
            @PathVariable String proposalId,
            Authentication authentication) {
        
        String userId = authentication.getName();
        ProposalResponse response = proposalService.acceptProposal(proposalId, userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{proposalId}/decline")
    public ResponseEntity<ProposalResponse> declineProposal(
            @PathVariable String proposalId,
            Authentication authentication) {
        
        String userId = authentication.getName();
        ProposalResponse response = proposalService.declineProposal(proposalId, userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{proposalId}/negotiate")
    public ResponseEntity<ProposalResponse> negotiateProposal(
            @PathVariable String proposalId,
            Authentication authentication) {
        
        String userId = authentication.getName();
        ProposalResponse response = proposalService.negotiateProposal(proposalId, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-proposals")
    public ResponseEntity<List<ProposalResponse>> getMyProposals(Authentication authentication) {
        String userId = authentication.getName();
        System.out.println("=== DEBUG: getMyProposals ===");
        System.out.println("User ID from authentication: " + userId);
        
        // Get user details to determine if they're a worker or client
        User user = userRepository.findByUsername(userId).orElse(null);
        boolean isWorker = user != null && user.hasRole(Role.ROLE_WORKER);
        
        System.out.println("User found: " + (user != null));
        System.out.println("Is Worker: " + isWorker);
        if (user != null) {
            System.out.println("User ID: " + user.getId());
            System.out.println("User roles: " + user.getRoles());
        }
        
        List<ProposalResponse> proposals;
        if (isWorker) {
            // For workers: get proposals where they are the workerId (recipient)
            System.out.println("Getting proposals for WORKER with ID: " + userId);
            proposals = proposalService.getProposalsForWorker(userId);
        } else {
            // For clients: get proposals where they are the clientId (sender)
            System.out.println("Getting proposals for CLIENT with ID: " + userId);
            proposals = proposalService.getProposalsForClient(userId);
        }
        
        System.out.println("Found " + proposals.size() + " proposals");
        proposals.forEach(p -> {
            System.out.println("Proposal - ID: " + p.getId() + 
                              ", ClientId: " + p.getClientId() + 
                              ", WorkerId: " + p.getWorkerId() + 
                              ", SentBy: " + p.getSentBy() + 
                              ", Status: " + p.getStatus());
        });
        
        return ResponseEntity.ok(proposals);
    }
    
    @GetMapping("/chat/{clientUsername}/{workerUsername}")
    public ResponseEntity<List<ProposalResponse>> getProposalsBetweenUsers(
            @PathVariable String clientUsername,
            @PathVariable String workerUsername) {
        
        System.out.println("DEBUG: Searching proposals between clientUsername: " + clientUsername + " and workerUsername: " + workerUsername);
        
        List<ProposalResponse> proposals = proposalService.getProposalsBetweenUsers(clientUsername, workerUsername);
        
        System.out.println("DEBUG: Found " + proposals.size() + " proposals between users");
        
        return ResponseEntity.ok(proposals);
    }
} 