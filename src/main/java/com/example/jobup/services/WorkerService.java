package com.example.jobup.services;

import com.example.jobup.dto.WorkerCreateDto;
import com.example.jobup.dto.WorkerUpdateDto;
import com.example.jobup.dto.WorkerResponseDto;
import com.example.jobup.entities.Worker;
import com.example.jobup.mapper.WorkerMapper;
import com.example.jobup.repositories.WorkerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class WorkerService implements IWorkerService {

    private final WorkerRepo workerRepo;
    private final WorkerMapper workerMapper;

    public List<WorkerResponseDto> getAllWorkers() {
        return workerRepo.findAll()
                .stream()
                .map(workerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<WorkerResponseDto> getWorkerById(String id) {
        return workerRepo.findById(id)
                .map(workerMapper::toResponseDto);
    }

    public WorkerResponseDto createWorker(WorkerCreateDto dto) {
        Worker entity = workerMapper.toEntity(dto);
        Worker saved = workerRepo.save(entity);
        return workerMapper.toResponseDto(saved);
    }

    public WorkerResponseDto updateWorker(String id, WorkerUpdateDto dto) {
        Worker existing = workerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        // Mise à jour partielle des champs
        workerMapper.updateWorkerFromDto(dto, existing);

        Worker updated = workerRepo.save(existing);
        return workerMapper.toResponseDto(updated);
    }

    public void deleteWorker(String id) {
        workerRepo.deleteById(id);
    }

    public List<WorkerResponseDto> searchByLocation(String location) {
        return workerRepo.findByLocationIgnoreCase(location)
                .stream()
                .map(workerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<WorkerResponseDto> searchByJobType(String jobType) {
        return workerRepo.findByJobTypeIgnoreCase(jobType)
                .stream()
                .map(workerMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
