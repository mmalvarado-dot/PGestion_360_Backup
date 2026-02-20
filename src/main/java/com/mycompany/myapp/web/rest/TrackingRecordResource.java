package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.repository.TrackingStats;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

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

    @GetMapping("")
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

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrackingRecordDTO>> getTrackingRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TrackingRecord : {}", id);
        return ResponseUtil.wrapOrNotFound(trackingRecordService.findOne(id));
    }

    /**
     * BUSCADOR: Obtiene el historial por ID de solicitud (Change Request)
     */
    @GetMapping("/request/{id}")
    public Mono<ResponseEntity<List<TrackingRecordDTO>>> getAllByRequestId(@PathVariable Long id) {
        LOG.debug("REST request to get TrackingHistory for ChangeRequest : {}", id);
        return trackingRecordService
            .findAllByRequestId(id) // <--- Asegúrate que este método exista en el Service
            .collectList()
            .map(items -> ResponseEntity.ok().body(items));
    }

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

    // --- ESTADÍSTICAS ---

    @GetMapping(value = "/stats/departments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TrackingStats> getDepartmentStats() {
        return trackingRecordService.getDepartmentStats();
    }

    @GetMapping(value = "/stats/responsibles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TrackingStats> getResponsibleStats() {
        return trackingRecordService.getResponsibleStats();
    }
}
