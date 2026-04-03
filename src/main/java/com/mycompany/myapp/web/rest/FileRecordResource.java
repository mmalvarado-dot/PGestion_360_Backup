package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.FileRecordRepository;
import com.mycompany.myapp.service.FileRecordService;
import com.mycompany.myapp.service.dto.FileRecordDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.FileRecord}.
 */
@RestController
@RequestMapping("/api/file-records")
public class FileRecordResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileRecordResource.class);

    private static final String ENTITY_NAME = "fileRecord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileRecordService fileRecordService;

    private final FileRecordRepository fileRecordRepository;

    public FileRecordResource(FileRecordService fileRecordService, FileRecordRepository fileRecordRepository) {
        this.fileRecordService = fileRecordService;
        this.fileRecordRepository = fileRecordRepository;
    }

    @PostMapping("")
    public Mono<ResponseEntity<FileRecordDTO>> createFileRecord(@Valid @RequestBody FileRecordDTO fileRecordDTO) throws URISyntaxException {
        LOG.debug("REST request to save FileRecord : {}", fileRecordDTO);
        if (fileRecordDTO.getId() != null) {
            throw new BadRequestAlertException("A new fileRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return fileRecordService
            .save(fileRecordDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/file-records/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<FileRecordDTO>> updateFileRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FileRecordDTO fileRecordDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FileRecord : {}, {}", id, fileRecordDTO);
        if (fileRecordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileRecordDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fileRecordRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return fileRecordService
                    .update(fileRecordDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<FileRecordDTO>> partialUpdateFileRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FileRecordDTO fileRecordDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FileRecord partially : {}, {}", id, fileRecordDTO);
        if (fileRecordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileRecordDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fileRecordRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<FileRecordDTO> result = fileRecordService.partialUpdate(fileRecordDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<FileRecordDTO>>> getAllFileRecords(
        @RequestParam(value = "changeRequestId.equals", required = false) Long changeRequestId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of FileRecords");

        Mono<Long> countMono;
        Mono<List<FileRecordDTO>> listMono;

        if (changeRequestId != null) {
            countMono = fileRecordService.countByChangeRequestId(changeRequestId);
            listMono = fileRecordService.findByChangeRequestId(changeRequestId, pageable).collectList();
        } else {
            countMono = fileRecordService.countAll();
            listMono = fileRecordService.findAll(pageable).collectList();
        }

        return countMono
            .zipWith(listMono)
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
    public Mono<ResponseEntity<FileRecordDTO>> getFileRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FileRecord : {}", id);
        Mono<FileRecordDTO> fileRecordDTO = fileRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fileRecordDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFileRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FileRecord : {}", id);
        return fileRecordService
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
