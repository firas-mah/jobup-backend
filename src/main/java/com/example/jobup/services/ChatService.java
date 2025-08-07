package com.example.jobup.services;

import com.example.jobup.dto.ChatMessageDto;
import com.example.jobup.entities.ChatMessage;
import com.example.jobup.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatMessageRepository chatMessageRepository;
    
    public List<ChatMessageDto> getChatMessages(String chatId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatIdOrderByTimestampAsc(chatId);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ChatMessageDto sendMessage(String chatId, String senderId, String senderName, 
                                   String senderType, String receiverId, String receiverName,
                                   String receiverType, String content, ChatMessage.MessageType messageType) {
        ChatMessage message = ChatMessage.builder()
                .chatId(chatId)
                .senderId(senderId)
                .senderName(senderName)
                .senderType(senderType)
                .receiverId(receiverId)
                .receiverName(receiverName)
                .receiverType(receiverType)
                .content(content)
                .messageType(messageType)
                .timestamp(LocalDateTime.now())
                .build();
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        return convertToDto(savedMessage);
    }
    
    public List<ChatMessageDto> getMessagesByReceiverId(String receiverId) {
        List<ChatMessage> messages = chatMessageRepository.findByReceiverIdOrderByTimestampDesc(receiverId);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ChatMessageDto> getMessagesByReceiverIdAndType(String receiverId, String receiverType) {
        List<ChatMessage> messages = chatMessageRepository.findByReceiverIdAndReceiverTypeOrderByTimestampDesc(receiverId, receiverType);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private ChatMessageDto convertToDto(ChatMessage message) {
        return ChatMessageDto.builder()
                .id(message.getId())
                .chatId(message.getChatId())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .senderType(message.getSenderType())
                .receiverId(message.getReceiverId())
                .receiverName(message.getReceiverName())
                .receiverType(message.getReceiverType())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .messageType(message.getMessageType())
                .proposalId(message.getProposalId())
                .build();
    }
} 