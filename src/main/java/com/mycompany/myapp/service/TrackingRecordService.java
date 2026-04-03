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

    // Métodos generales (Para el Admin)
    Flux<TrackingRecordDTO> findAll(Pageable pageable);
    Mono<Long> countAll();

    //   métodos de seguridad (Para el Usuario Normal)

    Flux<TrackingRecordDTO> findAllByUser(Pageable pageable, Long userId);
    Mono<Long> countAllByUser(Long userId);
    // ========================================================================

    Mono<TrackingRecordDTO> findOne(Long id);
    Mono<Void> delete(Long id);
    Flux<TrackingRecordDTO> findAllByRequestId(Long id);

    //  Método de estadísticas actualizados con parámetros de fecha

    Flux<TrackingStats> getDepartmentStats(Integer year, Integer month);
    Flux<TrackingStats> getUserStats(Integer year, Integer month);
}
