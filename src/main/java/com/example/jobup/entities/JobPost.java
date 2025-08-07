package com.example.jobup.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "job_posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPost {
    @Id
    private String id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime createdAt;
    private String createdById;
    private String createdByName;
    private List<String> likes = new ArrayList<>();
    private List<String> savedBy = new ArrayList<>();
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private String id;
        private String authorId;
        private String authorName;
        private String content;
        private LocalDateTime createdAt;
    }
}