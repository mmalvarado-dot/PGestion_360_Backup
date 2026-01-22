package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.CatalogueDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Catalogue}.
 */
public interface CatalogueService {
    /**
     * Save a catalogue.
     *
     * @param catalogueDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CatalogueDTO> save(CatalogueDTO catalogueDTO);

    /**
     * Updates a catalogue.
     *
     * @param catalogueDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CatalogueDTO> update(CatalogueDTO catalogueDTO);

    /**
     * Partially updates a catalogue.
     *
     * @param catalogueDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CatalogueDTO> partialUpdate(CatalogueDTO catalogueDTO);

    /**
     * Get all the catalogues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CatalogueDTO> findAll(Pageable pageable);

    /**
     * Returns the number of catalogues available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" catalogue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CatalogueDTO> findOne(Long id);

    /**
     * Delete the "id" catalogue.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
