package com.example.jobup.controller;

import com.example.jobup.dto.JobPostDto;
import com.example.jobup.services.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobPostController {
    private final JobPostService jobPostService;

    @PostMapping
    public JobPostDto createPost(@RequestBody JobPostDto dto) {
        return jobPostService.createPost(dto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JobPostDto> getAllPosts() {
        return jobPostService.getAllPosts();
    }

    @PostMapping("/{postId}/like")
    public JobPostDto likePost(@PathVariable String postId, @RequestParam String workerId) {
        return jobPostService.likePost(postId, workerId);
    }

    @PostMapping("/{postId}/save")
    public JobPostDto savePost(@PathVariable String postId, @RequestParam String workerId) {
        return jobPostService.savePost(postId, workerId);
    }

    @PostMapping("/{postId}/comments")
    public JobPostDto addComment(@PathVariable String postId, @RequestBody JobPostDto.CommentDto commentDto) {
        return jobPostService.addComment(postId, commentDto);
    }

    @GetMapping("/saved/{userId}")
    public List<JobPostDto> getSavedPostsByUserId(@PathVariable String userId) {
        return jobPostService.getSavedPostsByUserId(userId);
    }

    @GetMapping("/created-by/{userId}")
    public List<JobPostDto> getPostsByCreatorId(@PathVariable String userId) {
        return jobPostService.getPostsByCreatorId(userId);
    }
}