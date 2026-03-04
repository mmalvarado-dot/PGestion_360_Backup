package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.TrackingStats;
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrackingRecordService {
    Mono<TrackingRecordDTO> save(TrackingRecordDTO trackingRecordDTO);
    Mono<TrackingRecordDTO> update(TrackingRecordDTO trackingRecordDTO);
    Mono<TrackingRecordDTO> partialUpdate(TrackingRecordDTO trackingRecordDTO);
    Flux<TrackingRecordDTO> findAll(Pageable pageable);
    Mono<Long> countAll();
    Mono<TrackingRecordDTO> findOne(Long id);
    Mono<Void> delete(Long id);
    Flux<TrackingRecordDTO> findAllByRequestId(Long id);
    Flux<TrackingStats> getDepartmentStats();
    Flux<TrackingStats> getUserStats();
}
