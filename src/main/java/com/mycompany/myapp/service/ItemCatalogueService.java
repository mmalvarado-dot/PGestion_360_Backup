package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ItemCatalogueDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.ItemCatalogue}.
 */
public interface ItemCatalogueService {
    /**
     * Save a itemCatalogue.
     *
     * @param itemCatalogueDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ItemCatalogueDTO> save(ItemCatalogueDTO itemCatalogueDTO);

    /**
     * Updates a itemCatalogue.
     *
     * @param itemCatalogueDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ItemCatalogueDTO> update(ItemCatalogueDTO itemCatalogueDTO);

    /**
     * Partially updates a itemCatalogue.
     *
     * @param itemCatalogueDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ItemCatalogueDTO> partialUpdate(ItemCatalogueDTO itemCatalogueDTO);

    /**
     * Get all the itemCatalogues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ItemCatalogueDTO> findAll(Pageable pageable);

    /**
     * Returns the number of itemCatalogues available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" itemCatalogue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ItemCatalogueDTO> findOne(Long id);

    /**
     * Delete the "id" itemCatalogue.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
