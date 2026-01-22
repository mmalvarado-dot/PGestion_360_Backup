package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ChangeRequestRepository;
import com.mycompany.myapp.service.ChangeRequestService;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ChangeRequest}.
 */
@RestController
@RequestMapping("/api/change-requests")
public class ChangeRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeRequestResource.class);

    private static final String ENTITY_NAME = "changeRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChangeRequestService changeRequestService;

    private final ChangeRequestRepository changeRequestRepository;

    public ChangeRequestResource(ChangeRequestService changeRequestService, ChangeRequestRepository changeRequestRepository) {
        this.changeRequestService = changeRequestService;
        this.changeRequestRepository = changeRequestRepository;
    }

    /**
     * {@code POST  /change-requests} : Create a new changeRequest.
     *
     * @param changeRequestDTO the changeRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new changeRequestDTO, or with status {@code 400 (Bad Request)} if the changeRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ChangeRequestDTO>> createChangeRequest(@Valid @RequestBody ChangeRequestDTO changeRequestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ChangeRequest : {}", changeRequestDTO);
        if (changeRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new changeRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return changeRequestService
            .save(changeRequestDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/change-requests/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /change-requests/:id} : Updates an existing changeRequest.
     *
     * @param id the id of the changeRequestDTO to save.
     * @param changeRequestDTO the changeRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated changeRequestDTO,
     * or with status {@code 400 (Bad Request)} if the changeRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the changeRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ChangeRequestDTO>> updateChangeRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ChangeRequestDTO changeRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ChangeRequest : {}, {}", id, changeRequestDTO);
        if (changeRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, changeRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return changeRequestRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return changeRequestService
                    .update(changeRequestDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /change-requests/:id} : Partial updates given fields of an existing changeRequest, field will ignore if it is null
     *
     * @param id the id of the changeRequestDTO to save.
     * @param changeRequestDTO the changeRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated changeRequestDTO,
     * or with status {@code 400 (Bad Request)} if the changeRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the changeRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the changeRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ChangeRequestDTO>> partialUpdateChangeRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ChangeRequestDTO changeRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ChangeRequest partially : {}, {}", id, changeRequestDTO);
        if (changeRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, changeRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return changeRequestRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ChangeRequestDTO> result = changeRequestService.partialUpdate(changeRequestDTO);

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
     * {@code GET  /change-requests} : get all the changeRequests.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of changeRequests in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ChangeRequestDTO>>> getAllChangeRequests(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of ChangeRequests");
        return changeRequestService
            .countAll()
            .zipWith(changeRequestService.findAll(pageable).collectList())
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
     * {@code GET  /change-requests/:id} : get the "id" changeRequest.
     *
     * @param id the id of the changeRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the changeRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ChangeRequestDTO>> getChangeRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ChangeRequest : {}", id);
        Mono<ChangeRequestDTO> changeRequestDTO = changeRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(changeRequestDTO);
    }

    /**
     * {@code DELETE  /change-requests/:id} : delete the "id" changeRequest.
     *
     * @param id the id of the changeRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteChangeRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ChangeRequest : {}", id);
        return changeRequestService
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
