package com.example.jobup.services;

import com.example.jobup.dto.JobPostDto;
import com.example.jobup.entities.JobPost;
import com.example.jobup.repositories.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostService {
    private final JobPostRepository jobPostRepository;

    public JobPostDto createPost(JobPostDto dto) {
        JobPost post = JobPost.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .createdAt(LocalDateTime.now())
                .createdById(dto.getCreatedById())
                .createdByName(dto.getCreatedByName())
                .build();
        return toDto(jobPostRepository.save(post));
    }

    public List<JobPostDto> getAllPosts() {
        return jobPostRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public JobPostDto likePost(String postId, String workerId) {
        JobPost post = jobPostRepository.findById(postId).orElseThrow();
        if (post.getLikes().contains(workerId)) {
            post.getLikes().remove(workerId);
        } else {
            post.getLikes().add(workerId);
        }
        return toDto(jobPostRepository.save(post));
    }

    public JobPostDto savePost(String postId, String workerId) {
        JobPost post = jobPostRepository.findById(postId).orElseThrow();
        if (post.getSavedBy().contains(workerId)) {
            post.getSavedBy().remove(workerId);
        } else {
            post.getSavedBy().add(workerId);
        }
        return toDto(jobPostRepository.save(post));
    }

    public JobPostDto addComment(String postId, JobPostDto.CommentDto commentDto) {
        JobPost post = jobPostRepository.findById(postId).orElseThrow();
        JobPost.Comment comment = JobPost.Comment.builder()
                .id(UUID.randomUUID().toString())
                .authorId(commentDto.getAuthorId())
                .authorName(commentDto.getAuthorName())
                .content(commentDto.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        post.getComments().add(comment);
        return toDto(jobPostRepository.save(post));
    }

    private JobPostDto toDto(JobPost post) {
        return JobPostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .location(post.getLocation())
                .createdAt(post.getCreatedAt())
                .createdById(post.getCreatedById())
                .createdByName(post.getCreatedByName())
                .likes(post.getLikes())
                .savedBy(post.getSavedBy())
                .comments(post.getComments() != null ? post.getComments().stream().map(c ->
                        JobPostDto.CommentDto.builder()
                                .id(c.getId())
                                .authorId(c.getAuthorId())
                                .authorName(c.getAuthorName())
                                .content(c.getContent())
                                .createdAt(c.getCreatedAt())
                                .build()
                ).collect(Collectors.toList()) : new java.util.ArrayList<>())
                .build();
    }
}