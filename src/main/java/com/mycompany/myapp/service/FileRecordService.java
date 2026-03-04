package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.FileRecordDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.FileRecord}.
 */
public interface FileRecordService {
    /**
     * Save a fileRecord.
     *
     * @param fileRecordDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<FileRecordDTO> save(FileRecordDTO fileRecordDTO);

    /**
     * Updates a fileRecord.
     *
     * @param fileRecordDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<FileRecordDTO> update(FileRecordDTO fileRecordDTO);

    /**
     * Partially updates a fileRecord.
     *
     * @param fileRecordDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<FileRecordDTO> partialUpdate(FileRecordDTO fileRecordDTO);

    /**
     * Get all the fileRecords.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<FileRecordDTO> findAll(Pageable pageable);

    /**
     * Returns the number of fileRecords available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" fileRecord.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<FileRecordDTO> findOne(Long id);

    /**
     * Delete the "id" fileRecord.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    Flux<FileRecordDTO> findByChangeRequestId(Long changeRequestId, Pageable pageable);
    Mono<Long> countByChangeRequestId(Long changeRequestId);
}
