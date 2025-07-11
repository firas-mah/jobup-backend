package com.example.jobup.services;

import com.example.jobup.dto.WorkerCreateDto;
import com.example.jobup.dto.WorkerResponseDto;
import com.example.jobup.dto.WorkerUpdateDto;
import com.example.jobup.entities.Worker;

import java.util.List;
import java.util.Optional;

public interface IWorkerService {
    List<WorkerResponseDto> getAllWorkers() ;
    Optional<WorkerResponseDto> getWorkerById(String id) ;
    WorkerResponseDto createWorker(WorkerCreateDto dto) ;
    void deleteWorker(String id) ;
    List<WorkerResponseDto> searchByLocation(String location) ;
    List<WorkerResponseDto> searchByJobType(String jobType) ;
    WorkerResponseDto updateWorker(String id, WorkerUpdateDto dto);
    }
