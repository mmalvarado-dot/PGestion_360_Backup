package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.ChangeRequest}.
 */
public interface ChangeRequestService {
    /**
     * Save a changeRequest.
     *
     * @param changeRequestDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ChangeRequestDTO> save(ChangeRequestDTO changeRequestDTO);

    /**
     * Updates a changeRequest.
     *
     * @param changeRequestDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ChangeRequestDTO> update(ChangeRequestDTO changeRequestDTO);

    /**
     * Partially updates a changeRequest.
     *
     * @param changeRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ChangeRequestDTO> partialUpdate(ChangeRequestDTO changeRequestDTO);

    /**
     * Get all the changeRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ChangeRequestDTO> findAll(Pageable pageable);

    /**
     * Returns the number of changeRequests available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" changeRequest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ChangeRequestDTO> findOne(Long id);

    /**
     * Delete the "id" changeRequest.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
