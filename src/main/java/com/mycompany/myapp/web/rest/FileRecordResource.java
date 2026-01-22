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

    /**
     * {@code POST  /file-records} : Create a new fileRecord.
     *
     * @param fileRecordDTO the fileRecordDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileRecordDTO, or with status {@code 400 (Bad Request)} if the fileRecord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    /**
     * {@code PUT  /file-records/:id} : Updates an existing fileRecord.
     *
     * @param id the id of the fileRecordDTO to save.
     * @param fileRecordDTO the fileRecordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileRecordDTO,
     * or with status {@code 400 (Bad Request)} if the fileRecordDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fileRecordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    /**
     * {@code PATCH  /file-records/:id} : Partial updates given fields of an existing fileRecord, field will ignore if it is null
     *
     * @param id the id of the fileRecordDTO to save.
     * @param fileRecordDTO the fileRecordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileRecordDTO,
     * or with status {@code 400 (Bad Request)} if the fileRecordDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fileRecordDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fileRecordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    /**
     * {@code GET  /file-records} : get all the fileRecords.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fileRecords in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<FileRecordDTO>>> getAllFileRecords(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of FileRecords");
        return fileRecordService
            .countAll()
            .zipWith(fileRecordService.findAll(pageable).collectList())
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
     * {@code GET  /file-records/:id} : get the "id" fileRecord.
     *
     * @param id the id of the fileRecordDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileRecordDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FileRecordDTO>> getFileRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FileRecord : {}", id);
        Mono<FileRecordDTO> fileRecordDTO = fileRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fileRecordDTO);
    }

    /**
     * {@code DELETE  /file-records/:id} : delete the "id" fileRecord.
     *
     * @param id the id of the fileRecordDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
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
