package com.example.jobup.services;

import com.example.jobup.dto.JobProposalDto;
import com.example.jobup.entities.JobProposal;
import com.example.jobup.repositories.JobProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProposalService {
    
    private final JobProposalRepository proposalRepository;
    
    public JobProposalDto createProposal(String chatId, String senderId, String senderName, 
                                       String senderType, String title, String description, 
                                       Integer duration, java.math.BigDecimal price, 
                                       String location, LocalDateTime scheduledTime) {
        JobProposal proposal = JobProposal.builder()
                .chatId(chatId)
                .senderId(senderId)
                .senderName(senderName)
                .senderType(senderType)
                .title(title)
                .description(description)
                .duration(duration)
                .price(price)
                .location(location)
                .scheduledTime(scheduledTime)
                .createdAt(LocalDateTime.now())
                .status(JobProposal.ProposalStatus.PENDING)
                .build();
        
        JobProposal savedProposal = proposalRepository.save(proposal);
        return convertToDto(savedProposal);
    }
    
    public JobProposalDto updateProposalStatus(String proposalId, JobProposal.ProposalStatus status) {
        JobProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        proposal.setStatus(status);
        JobProposal savedProposal = proposalRepository.save(proposal);
        return convertToDto(savedProposal);
    }
    
    public List<JobProposalDto> getProposalsByChatId(String chatId) {
        List<JobProposal> proposals = proposalRepository.findByChatIdOrderByCreatedAtDesc(chatId);
        return proposals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private JobProposalDto convertToDto(JobProposal proposal) {
        return JobProposalDto.builder()
                .id(proposal.getId())
                .chatId(proposal.getChatId())
                .senderId(proposal.getSenderId())
                .senderName(proposal.getSenderName())
                .senderType(proposal.getSenderType())
                .title(proposal.getTitle())
                .description(proposal.getDescription())
                .duration(proposal.getDuration())
                .price(proposal.getPrice())
                .location(proposal.getLocation())
                .scheduledTime(proposal.getScheduledTime())
                .createdAt(proposal.getCreatedAt())
                .status(proposal.getStatus())
                .build();
    }
} 