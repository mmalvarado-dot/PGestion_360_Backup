package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ResponsibleDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Responsible}.
 */
public interface ResponsibleService {
    /**
     * Save a responsible.
     *
     * @param responsibleDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ResponsibleDTO> save(ResponsibleDTO responsibleDTO);

    /**
     * Updates a responsible.
     *
     * @param responsibleDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ResponsibleDTO> update(ResponsibleDTO responsibleDTO);

    /**
     * Partially updates a responsible.
     *
     * @param responsibleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ResponsibleDTO> partialUpdate(ResponsibleDTO responsibleDTO);

    /**
     * Get all the responsibles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ResponsibleDTO> findAll(Pageable pageable);

    /**
     * Returns the number of responsibles available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" responsible.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ResponsibleDTO> findOne(Long id);

    /**
     * Delete the "id" responsible.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
