package com.example.jobup.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;

@Document(collection = "worker") // MongoDB annotation
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Worker {
    @Id
    private String id;

    private String fullName;
    private String jobType;       // e.g. "electrician", "plumber"
    private String phoneNumber;
    private String location;      // e.g. "Ariana", "Sfax"
    private double rating;        // average rating
    private String description;

    // File references for portfolio and certificates
    @Builder.Default
    private List<String> portfolioFileIds = new ArrayList<>();

    @Builder.Default
    private List<String> certificateFileIds = new ArrayList<>();

}
