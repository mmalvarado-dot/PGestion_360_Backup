package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ResponsibleRepository;
import com.mycompany.myapp.service.ResponsibleService;
import com.mycompany.myapp.service.dto.ResponsibleDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Responsible}.
 */
@RestController
@RequestMapping("/api/responsibles")
public class ResponsibleResource {

    private static final Logger LOG = LoggerFactory.getLogger(ResponsibleResource.class);

    private static final String ENTITY_NAME = "responsible";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResponsibleService responsibleService;

    private final ResponsibleRepository responsibleRepository;

    public ResponsibleResource(ResponsibleService responsibleService, ResponsibleRepository responsibleRepository) {
        this.responsibleService = responsibleService;
        this.responsibleRepository = responsibleRepository;
    }

    /**
     * {@code POST  /responsibles} : Create a new responsible.
     *
     * @param responsibleDTO the responsibleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new responsibleDTO, or with status {@code 400 (Bad Request)} if the responsible has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ResponsibleDTO>> createResponsible(@Valid @RequestBody ResponsibleDTO responsibleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Responsible : {}", responsibleDTO);
        if (responsibleDTO.getId() != null) {
            throw new BadRequestAlertException("A new responsible cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return responsibleService
            .save(responsibleDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/responsibles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /responsibles/:id} : Updates an existing responsible.
     *
     * @param id the id of the responsibleDTO to save.
     * @param responsibleDTO the responsibleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated responsibleDTO,
     * or with status {@code 400 (Bad Request)} if the responsibleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the responsibleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ResponsibleDTO>> updateResponsible(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ResponsibleDTO responsibleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Responsible : {}, {}", id, responsibleDTO);
        if (responsibleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, responsibleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return responsibleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return responsibleService
                    .update(responsibleDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /responsibles/:id} : Partial updates given fields of an existing responsible, field will ignore if it is null
     *
     * @param id the id of the responsibleDTO to save.
     * @param responsibleDTO the responsibleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated responsibleDTO,
     * or with status {@code 400 (Bad Request)} if the responsibleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the responsibleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the responsibleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ResponsibleDTO>> partialUpdateResponsible(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ResponsibleDTO responsibleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Responsible partially : {}, {}", id, responsibleDTO);
        if (responsibleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, responsibleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return responsibleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ResponsibleDTO> result = responsibleService.partialUpdate(responsibleDTO);

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
     * {@code GET  /responsibles} : get all the responsibles.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of responsibles in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ResponsibleDTO>>> getAllResponsibles(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Responsibles");
        return responsibleService
            .countAll()
            .zipWith(responsibleService.findAll(pageable).collectList())
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
     * {@code GET  /responsibles/:id} : get the "id" responsible.
     *
     * @param id the id of the responsibleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the responsibleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponsibleDTO>> getResponsible(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Responsible : {}", id);
        Mono<ResponsibleDTO> responsibleDTO = responsibleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(responsibleDTO);
    }

    /**
     * {@code DELETE  /responsibles/:id} : delete the "id" responsible.
     *
     * @param id the id of the responsibleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteResponsible(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Responsible : {}", id);
        return responsibleService
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
