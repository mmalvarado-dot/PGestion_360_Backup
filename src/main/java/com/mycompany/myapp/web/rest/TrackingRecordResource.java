package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.service.TrackingRecordService;
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrackingRecord}.
 */
@RestController
@RequestMapping("/api/tracking-records")
public class TrackingRecordResource {

    private static final Logger LOG = LoggerFactory.getLogger(TrackingRecordResource.class);

    private static final String ENTITY_NAME = "trackingRecord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrackingRecordService trackingRecordService;

    private final TrackingRecordRepository trackingRecordRepository;

    public TrackingRecordResource(TrackingRecordService trackingRecordService, TrackingRecordRepository trackingRecordRepository) {
        this.trackingRecordService = trackingRecordService;
        this.trackingRecordRepository = trackingRecordRepository;
    }

    /**
     * {@code POST  /tracking-records} : Create a new trackingRecord.
     *
     * @param trackingRecordDTO the trackingRecordDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackingRecordDTO, or with status {@code 400 (Bad Request)} if the trackingRecord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrackingRecordDTO>> createTrackingRecord(@Valid @RequestBody TrackingRecordDTO trackingRecordDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TrackingRecord : {}", trackingRecordDTO);
        if (trackingRecordDTO.getId() != null) {
            throw new BadRequestAlertException("A new trackingRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trackingRecordService
            .save(trackingRecordDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/tracking-records/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tracking-records/:id} : Updates an existing trackingRecord.
     *
     * @param id the id of the trackingRecordDTO to save.
     * @param trackingRecordDTO the trackingRecordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackingRecordDTO,
     * or with status {@code 400 (Bad Request)} if the trackingRecordDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trackingRecordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrackingRecordDTO>> updateTrackingRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrackingRecordDTO trackingRecordDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TrackingRecord : {}, {}", id, trackingRecordDTO);
        if (trackingRecordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trackingRecordDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trackingRecordRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trackingRecordService
                    .update(trackingRecordDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /tracking-records/:id} : Partial updates given fields of an existing trackingRecord, field will ignore if it is null
     *
     * @param id the id of the trackingRecordDTO to save.
     * @param trackingRecordDTO the trackingRecordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackingRecordDTO,
     * or with status {@code 400 (Bad Request)} if the trackingRecordDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trackingRecordDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trackingRecordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrackingRecordDTO>> partialUpdateTrackingRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrackingRecordDTO trackingRecordDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TrackingRecord partially : {}, {}", id, trackingRecordDTO);
        if (trackingRecordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trackingRecordDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trackingRecordRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrackingRecordDTO> result = trackingRecordService.partialUpdate(trackingRecordDTO);

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
     * {@code GET  /tracking-records} : get all the trackingRecords.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackingRecords in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrackingRecordDTO>>> getAllTrackingRecords(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of TrackingRecords");
        return trackingRecordService
            .countAll()
            .zipWith(trackingRecordService.findAll(pageable).collectList())
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
     * {@code GET  /tracking-records/:id} : get the "id" trackingRecord.
     *
     * @param id the id of the trackingRecordDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackingRecordDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrackingRecordDTO>> getTrackingRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TrackingRecord : {}", id);
        Mono<TrackingRecordDTO> trackingRecordDTO = trackingRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trackingRecordDTO);
    }

    /**
     * {@code DELETE  /tracking-records/:id} : delete the "id" trackingRecord.
     *
     * @param id the id of the trackingRecordDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrackingRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TrackingRecord : {}", id);
        return trackingRecordService
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
