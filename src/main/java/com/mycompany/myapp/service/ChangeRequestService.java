package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.ChangeRequest}.
 */
public interface ChangeRequestService {
    Mono<ChangeRequestDTO> save(ChangeRequestDTO changeRequestDTO);

    Mono<ChangeRequestDTO> update(ChangeRequestDTO changeRequestDTO);

    Mono<ChangeRequestDTO> partialUpdate(ChangeRequestDTO changeRequestDTO);

    Flux<ChangeRequestDTO> findAll(Pageable pageable);

    Mono<Long> countAll();

    Mono<ChangeRequestDTO> findOne(Long id);

    Mono<Void> delete(Long id);

    //   MÉTODOS PARA EL BUSCADOR CON FILTROS

    Flux<ChangeRequestDTO> findByFilters(String search, String status, Instant startDate, Instant endDate, Pageable pageable);

    Mono<Long> countByFilters(String search, String status, Instant startDate, Instant endDate);

    //   REGISTRO DE ARCHIVOS ADJUNTOS EN EL HISTORIAL

    Mono<Void> recordFileTracking(Long id, String fileName);
}
