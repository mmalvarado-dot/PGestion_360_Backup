package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ItemCatalogueRepository;
import com.mycompany.myapp.service.ItemCatalogueService;
import com.mycompany.myapp.service.dto.ItemCatalogueDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.ItemCatalogue}.
 */
@RestController
@RequestMapping("/api/item-catalogues")
public class ItemCatalogueResource {

    private static final Logger LOG = LoggerFactory.getLogger(ItemCatalogueResource.class);

    private static final String ENTITY_NAME = "itemCatalogue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ItemCatalogueService itemCatalogueService;

    private final ItemCatalogueRepository itemCatalogueRepository;

    public ItemCatalogueResource(ItemCatalogueService itemCatalogueService, ItemCatalogueRepository itemCatalogueRepository) {
        this.itemCatalogueService = itemCatalogueService;
        this.itemCatalogueRepository = itemCatalogueRepository;
    }

    /**
     * {@code POST  /item-catalogues} : Create a new itemCatalogue.
     *
     * @param itemCatalogueDTO the itemCatalogueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new itemCatalogueDTO, or with status {@code 400 (Bad Request)} if the itemCatalogue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ItemCatalogueDTO>> createItemCatalogue(@Valid @RequestBody ItemCatalogueDTO itemCatalogueDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ItemCatalogue : {}", itemCatalogueDTO);
        if (itemCatalogueDTO.getId() != null) {
            throw new BadRequestAlertException("A new itemCatalogue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return itemCatalogueService
            .save(itemCatalogueDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/item-catalogues/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /item-catalogues/:id} : Updates an existing itemCatalogue.
     *
     * @param id the id of the itemCatalogueDTO to save.
     * @param itemCatalogueDTO the itemCatalogueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemCatalogueDTO,
     * or with status {@code 400 (Bad Request)} if the itemCatalogueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the itemCatalogueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ItemCatalogueDTO>> updateItemCatalogue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ItemCatalogueDTO itemCatalogueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ItemCatalogue : {}, {}", id, itemCatalogueDTO);
        if (itemCatalogueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemCatalogueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return itemCatalogueRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return itemCatalogueService
                    .update(itemCatalogueDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /item-catalogues/:id} : Partial updates given fields of an existing itemCatalogue, field will ignore if it is null
     *
     * @param id the id of the itemCatalogueDTO to save.
     * @param itemCatalogueDTO the itemCatalogueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemCatalogueDTO,
     * or with status {@code 400 (Bad Request)} if the itemCatalogueDTO is not valid,
     * or with status {@code 404 (Not Found)} if the itemCatalogueDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the itemCatalogueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ItemCatalogueDTO>> partialUpdateItemCatalogue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ItemCatalogueDTO itemCatalogueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ItemCatalogue partially : {}, {}", id, itemCatalogueDTO);
        if (itemCatalogueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemCatalogueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return itemCatalogueRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ItemCatalogueDTO> result = itemCatalogueService.partialUpdate(itemCatalogueDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /item-catalogues} : get all the itemCatalogues.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of itemCatalogues in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ItemCatalogueDTO>>> getAllItemCatalogues(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of ItemCatalogues");
        return itemCatalogueService
            .countAll()
            .zipWith(itemCatalogueService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /item-catalogues/:id} : get the "id" itemCatalogue.
     *
     * @param id the id of the itemCatalogueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the itemCatalogueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ItemCatalogueDTO>> getItemCatalogue(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ItemCatalogue : {}", id);
        Mono<ItemCatalogueDTO> itemCatalogueDTO = itemCatalogueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(itemCatalogueDTO);
    }

    /**
     * {@code DELETE  /item-catalogues/:id} : delete the "id" itemCatalogue.
     *
     * @param id the id of the itemCatalogueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteItemCatalogue(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ItemCatalogue : {}", id);
        return itemCatalogueService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
