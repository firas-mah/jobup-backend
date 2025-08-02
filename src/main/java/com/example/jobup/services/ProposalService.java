package com.example.jobup.services;

import com.example.jobup.dto.CreateProposalRequest;
import com.example.jobup.dto.ProposalResponse;
import com.example.jobup.entities.Proposal;
import com.example.jobup.entities.ProposalStatus;
import com.example.jobup.entities.ChatMessage;
import com.example.jobup.entities.MessageType;
import com.example.jobup.repositories.ProposalRepository;
import com.example.jobup.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.example.jobup.entities.User;
import com.example.jobup.repositories.UserRepository;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProposalService {
    
    private final ProposalRepository proposalRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    
    public ProposalResponse createProposal(CreateProposalRequest request, String senderId, String senderType) {
        Proposal proposal = Proposal.builder()
                .clientId(request.getWorkerId()) // This will be updated based on sender type
                .workerId(request.getWorkerId())
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .price(request.getPrice())
                .location(request.getLocation())
                .scheduledTime(request.getScheduledTime())
                .status(ProposalStatus.PENDING)
                .sentBy(senderType)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Set clientId and workerId based on sender type
        if ("CLIENT".equals(senderType)) {
            proposal.setClientId(senderId);
        } else if ("WORKER".equals(senderType)) {
            proposal.setWorkerId(senderId);
        }
        
        Proposal savedProposal = proposalRepository.save(proposal);
        
        // Create chat message for the proposal
        String chatId = generateChatId(proposal.getClientId(), proposal.getWorkerId());
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(chatId)
                .senderId(senderId)
                .senderName(senderType)
                .senderType(senderType)
                .content("Sent a proposal: " + proposal.getTitle())
                .timestamp(LocalDateTime.now())
                .messageType(MessageType.PROPOSAL)
                .proposalId(savedProposal.getId())
                .build();
        
        chatMessageRepository.save(chatMessage);
        
        return mapToResponse(savedProposal);
    }
    
    public ProposalResponse acceptProposal(String proposalId, String userId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        proposal.setStatus(ProposalStatus.ACCEPTED);
        proposal.setAcceptedBy(userId);
        proposal.setAcceptedAt(LocalDateTime.now());
        proposal.setUpdatedAt(LocalDateTime.now());
        
        Proposal savedProposal = proposalRepository.save(proposal);
        
        // Create chat message for acceptance
        String chatId = generateChatId(proposal.getClientId(), proposal.getWorkerId());
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(chatId)
                .senderId(userId)
                .senderName("User")
                .senderType("CLIENT") // This should be determined based on user role
                .content("Accepted the proposal")
                .timestamp(LocalDateTime.now())
                .messageType(MessageType.PROPOSAL_RESPONSE)
                .proposalId(proposalId)
                .build();
        
        chatMessageRepository.save(chatMessage);
        
        return mapToResponse(savedProposal);
    }
    
    public ProposalResponse declineProposal(String proposalId, String userId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        proposal.setStatus(ProposalStatus.DECLINED);
        proposal.setDeclinedBy(userId);
        proposal.setDeclinedAt(LocalDateTime.now());
        proposal.setUpdatedAt(LocalDateTime.now());
        
        Proposal savedProposal = proposalRepository.save(proposal);
        
        // Create chat message for decline
        String chatId = generateChatId(proposal.getClientId(), proposal.getWorkerId());
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(chatId)
                .senderId(userId)
                .senderName("User")
                .senderType("CLIENT")
                .content("Declined the proposal")
                .timestamp(LocalDateTime.now())
                .messageType(MessageType.PROPOSAL_RESPONSE)
                .proposalId(proposalId)
                .build();
        
        chatMessageRepository.save(chatMessage);
        
        return mapToResponse(savedProposal);
    }
    
    public ProposalResponse negotiateProposal(String proposalId, String userId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        proposal.setStatus(ProposalStatus.NEGOTIATING);
        proposal.setUpdatedAt(LocalDateTime.now());
        
        Proposal savedProposal = proposalRepository.save(proposal);
        
        // Create chat message for negotiation
        String chatId = generateChatId(proposal.getClientId(), proposal.getWorkerId());
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(chatId)
                .senderId(userId)
                .senderName("User")
                .senderType("CLIENT")
                .content("Wants to negotiate the proposal")
                .timestamp(LocalDateTime.now())
                .messageType(MessageType.PROPOSAL_RESPONSE)
                .proposalId(proposalId)
                .build();
        
        chatMessageRepository.save(chatMessage);
        
        return mapToResponse(savedProposal);
    }
    
    public List<ProposalResponse> getProposalsForUser(String userId) {
        // For workers: get proposals where they are the workerId (recipient)
        // For clients: get proposals where they are the clientId (sender)
        List<Proposal> proposals = proposalRepository.findActiveProposalsForUser(userId);
        
        System.out.println("DEBUG: Found " + proposals.size() + " proposals for user " + userId);
        proposals.forEach(p -> {
            System.out.println("DEBUG: Proposal - ID: " + p.getId() + 
                              ", ClientId: " + p.getClientId() + 
                              ", WorkerId: " + p.getWorkerId() + 
                              ", SentBy: " + p.getSentBy());
        });
        
        return proposals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ProposalResponse> getProposalsBetweenUsers(String clientUsername, String workerUsername) {
        System.out.println("=== DEBUG: getProposalsBetweenUsers ===");
        System.out.println("Searching for proposals between clientUsername: " + clientUsername + " and workerUsername: " + workerUsername);
        
        // Get ObjectIds for both users
        User clientUser = userRepository.findByUsername(clientUsername).orElse(null);
        User workerUser = userRepository.findByUsername(workerUsername).orElse(null);
        
        if (clientUser == null || workerUser == null) {
            System.out.println("One or both users not found");
            return new ArrayList<>();
        }
        
        String clientObjectId = clientUser.getId();
        String workerObjectId = workerUser.getId();
        
        System.out.println("Client ObjectId: " + clientObjectId);
        System.out.println("Worker ObjectId: " + workerObjectId);
        
        // Get proposals between these users (using ObjectIds)
        List<Proposal> proposals = proposalRepository.findByClientIdAndWorkerIdOrderByCreatedAtDesc(clientObjectId, workerObjectId);
        
        System.out.println("Found " + proposals.size() + " proposals between users");
        proposals.forEach(p -> {
            System.out.println("DB Proposal - ID: " + p.getId() + 
                              ", ClientId: " + p.getClientId() + 
                              ", WorkerId: " + p.getWorkerId() + 
                              ", SentBy: " + p.getSentBy() + 
                              ", Status: " + p.getStatus());
        });
        
        return proposals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProposalResponse> getProposalsForWorker(String username) {
        System.out.println("=== DEBUG: getProposalsForWorker ===");
        System.out.println("Searching for proposals where workerId = " + username);
        
        // First, get the user's ObjectId
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            System.out.println("User not found: " + username);
            return new ArrayList<>();
        }
        
        String workerObjectId = user.getId();
        System.out.println("User ObjectId: " + workerObjectId);
        
        // Get proposals where this worker is the RECIPIENT (using ObjectId)
        List<Proposal> proposals = proposalRepository.findByWorkerIdOrderByCreatedAtDesc(workerObjectId);
        
        System.out.println("Found " + proposals.size() + " proposals in database");
        proposals.forEach(p -> {
            System.out.println("DB Proposal - ID: " + p.getId() + 
                              ", ClientId: " + p.getClientId() + 
                              ", WorkerId: " + p.getWorkerId() + 
                              ", SentBy: " + p.getSentBy() + 
                              ", Status: " + p.getStatus());
        });
        
        return proposals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProposalResponse> getProposalsForClient(String username) {
        System.out.println("=== DEBUG: getProposalsForClient ===");
        System.out.println("Searching for proposals where clientId = " + username);
        
        // First, get the user's ObjectId
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            System.out.println("User not found: " + username);
            return new ArrayList<>();
        }
        
        String clientObjectId = user.getId();
        System.out.println("User ObjectId: " + clientObjectId);
        
        // Get proposals where this client is the SENDER (using ObjectId)
        List<Proposal> proposals = proposalRepository.findByClientIdOrderByCreatedAtDesc(clientObjectId);
        
        System.out.println("Found " + proposals.size() + " proposals in database");
        proposals.forEach(p -> {
            System.out.println("DB Proposal - ID: " + p.getId() + 
                              ", ClientId: " + p.getClientId() + 
                              ", WorkerId: " + p.getWorkerId() + 
                              ", SentBy: " + p.getSentBy() + 
                              ", Status: " + p.getStatus());
        });
        
        return proposals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private String generateChatId(String clientId, String workerId) {
        // Create a consistent chat ID for the client-worker pair
        // Use ObjectIds consistently
        return clientId.compareTo(workerId) < 0 
                ? clientId + "_" + workerId 
                : workerId + "_" + clientId;
    }
    
    private ProposalResponse mapToResponse(Proposal proposal) {
        ProposalResponse response = new ProposalResponse();
        response.setId(proposal.getId());
        response.setClientId(proposal.getClientId());
        response.setWorkerId(proposal.getWorkerId());
        response.setTitle(proposal.getTitle());
        response.setDescription(proposal.getDescription());
        response.setDuration(proposal.getDuration());
        response.setPrice(proposal.getPrice());
        response.setLocation(proposal.getLocation());
        response.setScheduledTime(proposal.getScheduledTime());
        response.setStatus(proposal.getStatus());
        response.setCreatedAt(proposal.getCreatedAt());
        response.setUpdatedAt(proposal.getUpdatedAt());
        response.setSentBy(proposal.getSentBy());
        response.setAcceptedBy(proposal.getAcceptedBy());
        response.setAcceptedAt(proposal.getAcceptedAt());
        response.setDeclinedBy(proposal.getDeclinedBy());
        response.setDeclinedAt(proposal.getDeclinedAt());
        response.setCompletedBy(proposal.getCompletedBy());
        response.setCompletedAt(proposal.getCompletedAt());
        response.setRating(proposal.getRating());
        response.setFeedback(proposal.getFeedback());
        response.setRatedAt(proposal.getRatedAt());
        return response;
    }
} 