package com.example.jobup.services;

import com.example.jobup.entities.ChatMessage;
import com.example.jobup.entities.JobProposal;
import com.example.jobup.repositories.ChatMessageRepository;
import com.example.jobup.repositories.JobProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationService implements CommandLineRunner {
    
    private final JobProposalRepository proposalRepository;
    private final ChatMessageRepository messageRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Starting database migration...");
        migrateProposals();
        migrateChatMessages();
        log.info("Database migration completed.");
    }
    
    private void migrateProposals() {
        List<JobProposal> proposals = proposalRepository.findAll();
        int migratedCount = 0;
        
        for (JobProposal proposal : proposals) {
            if (proposal.getReceiverId() == null) {
                try {
                    // Extract receiver information from chatId
                    // Assuming chatId format: "clientId_workerId"
                    String[] chatIdParts = proposal.getChatId().split("_");
                    if (chatIdParts.length == 2) {
                        String clientId = chatIdParts[0];
                        String workerId = chatIdParts[1];
                        
                        if ("CLIENT".equals(proposal.getSenderType())) {
                            // If sender is CLIENT, receiver should be WORKER
                            proposal.setReceiverId(workerId);
                            proposal.setReceiverName("Worker");
                            proposal.setReceiverType("WORKER");
                        } else {
                            // If sender is WORKER, receiver should be CLIENT
                            proposal.setReceiverId(clientId);
                            proposal.setReceiverName("Client");
                            proposal.setReceiverType("CLIENT");
                        }
                        
                        proposalRepository.save(proposal);
                        migratedCount++;
                        log.info("Migrated proposal: {}", proposal.getId());
                    }
                } catch (Exception e) {
                    log.error("Error migrating proposal {}: {}", proposal.getId(), e.getMessage());
                }
            }
        }
        
        log.info("Migrated {} proposals", migratedCount);
    }
    
    private void migrateChatMessages() {
        List<ChatMessage> messages = messageRepository.findAll();
        int migratedCount = 0;
        
        for (ChatMessage message : messages) {
            if (message.getReceiverId() == null) {
                try {
                    // Extract receiver information from chatId
                    String[] chatIdParts = message.getChatId().split("_");
                    if (chatIdParts.length == 2) {
                        String clientId = chatIdParts[0];
                        String workerId = chatIdParts[1];
                        
                        if ("CLIENT".equals(message.getSenderType())) {
                            // If sender is CLIENT, receiver should be WORKER
                            message.setReceiverId(workerId);
                            message.setReceiverName("Worker");
                            message.setReceiverType("WORKER");
                        } else {
                            // If sender is WORKER, receiver should be CLIENT
                            message.setReceiverId(clientId);
                            message.setReceiverName("Client");
                            message.setReceiverType("CLIENT");
                        }
                        
                        messageRepository.save(message);
                        migratedCount++;
                        log.info("Migrated chat message: {}", message.getId());
                    }
                } catch (Exception e) {
                    log.error("Error migrating chat message {}: {}", message.getId(), e.getMessage());
                }
            }
        }
        
        log.info("Migrated {} chat messages", migratedCount);
    }
} 