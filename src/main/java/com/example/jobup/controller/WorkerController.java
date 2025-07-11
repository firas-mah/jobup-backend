package com.example.jobup.controller;

import com.example.jobup.dto.WorkerCreateDto;
import com.example.jobup.dto.WorkerUpdateDto;
import com.example.jobup.dto.WorkerResponseDto;
import com.example.jobup.services.IWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkerController {
    private final IWorkerService workerService;

    // 🔹 GET all workers
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WorkerResponseDto>> getAllWorkers() {
        return ResponseEntity.ok(workerService.getAllWorkers());
    }

    // 🔹 GET worker by ID
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkerResponseDto> getWorkerById(@PathVariable String id) {
        return workerService.getWorkerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 POST create new worker
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkerResponseDto> createWorker(@RequestBody WorkerCreateDto dto) {
        return ResponseEntity.ok(workerService.createWorker(dto));
    }

    // 🔹 PUT update existing worker
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkerResponseDto> updateWorker(@PathVariable String id, @RequestBody WorkerUpdateDto dto) {
        return ResponseEntity.ok(workerService.updateWorker(id, dto));
    }

    // 🔹 DELETE worker
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteWorker(@PathVariable String id) {
        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Search by location
    @GetMapping(value = "/search/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WorkerResponseDto>> searchByLocation(@RequestParam String location) {
        return ResponseEntity.ok(workerService.searchByLocation(location));
    }

    // 🔹 Search by job type
    @GetMapping(value = "/search/job", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WorkerResponseDto>> searchByJobType(@RequestParam String jobType) {
        return ResponseEntity.ok(workerService.searchByJobType(jobType));
    }
}
