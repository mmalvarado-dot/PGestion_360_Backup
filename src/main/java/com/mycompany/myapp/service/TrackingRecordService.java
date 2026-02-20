package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.TrackingStats; // <--- OJO: Necesitas importar esto del Repositorio
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrackingRecord}.
 */
public interface TrackingRecordService {
    /**
     * Save a trackingRecord.
     *
     * @param trackingRecordDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrackingRecordDTO> save(TrackingRecordDTO trackingRecordDTO);

    /**
     * Updates a trackingRecord.
     *
     * @param trackingRecordDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrackingRecordDTO> update(TrackingRecordDTO trackingRecordDTO);

    /**
     * Partially updates a trackingRecord.
     *
     * @param trackingRecordDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrackingRecordDTO> partialUpdate(TrackingRecordDTO trackingRecordDTO);

    /**
     * Get all the trackingRecords.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrackingRecordDTO> findAll(Pageable pageable);

    /**
     * Returns the number of trackingRecords available.
     * @return the number of entities in the database.
     */
    Mono<Long> countAll();

    /**
     * Get the "id" trackingRecord.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrackingRecordDTO> findOne(Long id);

    /**
     * Delete the "id" trackingRecord.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    // =================================================================
    //  MÉTODOS AGREGADOS PARA TU PANTALLA (Historial + Gráficos)
    // =================================================================

    /**
     * 1. PARA LA TABLA DE ABAJO (HISTORIAL)
     * Get all trackingRecords for a specific changeRequestId.
     */
    Flux<TrackingRecordDTO> findAllByRequestId(Long changeRequestId);

    /**
     * 2. PARA EL GRÁFICO SUPERIOR IZQUIERDO (TOP DEPTOS)
     * Devuelve ID de Depto y Cantidad
     */
    Flux<TrackingStats> getDepartmentStats();

    /**
     * 3. PARA EL GRÁFICO SUPERIOR DERECHO (TOP RESPONSABLES)
     * Devuelve ID de Responsable y Cantidad
     */
    Flux<TrackingStats> getResponsibleStats();
}
