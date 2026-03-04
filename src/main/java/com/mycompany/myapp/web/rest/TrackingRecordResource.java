package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.repository.TrackingStats;
import com.mycompany.myapp.service.TrackingRecordService;
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
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
    ) {
        if (trackingRecordDTO.getId() == null || !Objects.equals(id, trackingRecordDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        return trackingRecordRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                return trackingRecordService
                    .update(trackingRecordDTO)
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    @GetMapping("")
    public Mono<ResponseEntity<List<TrackingRecordDTO>>> getAllTrackingRecords(Pageable pageable, ServerHttpRequest request) {
        return trackingRecordService
            .countAll()
            .zipWith(trackingRecordService.findAll(pageable).collectList())
            .map(tuple ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(tuple.getT2(), pageable, tuple.getT1())
                        )
                    )
                    .body(tuple.getT2())
            );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrackingRecordDTO>> getTrackingRecord(@PathVariable("id") Long id) {
        return ResponseUtil.wrapOrNotFound(trackingRecordService.findOne(id));
    }

    @GetMapping("/request/{id}")
    public Mono<ResponseEntity<List<TrackingRecordDTO>>> getAllByRequestId(@PathVariable Long id) {
        return trackingRecordService.findAllByRequestId(id).collectList().map(items -> ResponseEntity.ok().body(items));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrackingRecord(@PathVariable("id") Long id) {
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

    @GetMapping(value = "/stats/departments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TrackingStats> getDepartmentStats() {
        return trackingRecordService.getDepartmentStats();
    }

    @GetMapping(value = "/stats/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TrackingStats> getUserStats() {
        return trackingRecordService.getUserStats();
    }
}
