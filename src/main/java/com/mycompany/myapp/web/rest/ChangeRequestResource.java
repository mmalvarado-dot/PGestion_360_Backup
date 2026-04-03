package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ChangeRequestRepository;
import com.mycompany.myapp.service.ChangeRequestService;
import com.mycompany.myapp.service.FileStorageService;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

@RestController
@RequestMapping("/api/change-requests")
public class ChangeRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeRequestResource.class);
    private static final String ENTITY_NAME = "changeRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChangeRequestService changeRequestService;
    private final ChangeRequestRepository changeRequestRepository;
    private final FileStorageService fileStorageService;
    private final DatabaseClient databaseClient;

    public ChangeRequestResource(
        ChangeRequestService changeRequestService,
        ChangeRequestRepository changeRequestRepository,
        FileStorageService fileStorageService,
        DatabaseClient databaseClient
    ) {
        this.changeRequestService = changeRequestService;
        this.changeRequestRepository = changeRequestRepository;
        this.fileStorageService = fileStorageService;
        this.databaseClient = databaseClient;
    }

    @PostMapping(value = "/{id}/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> uploadFile(@PathVariable Long id, @RequestPart("file") FilePart filePart) {
        LOG.debug("REST request para subir un archivo para ChangeRequest ID: {}", id);

        return changeRequestRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

                return DataBufferUtils.join(filePart.content())
                    .map(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        return bytes;
                    })
                    .flatMap(bytes -> {
                        String fileName = filePart.filename() != null ? filePart.filename() : "archivo_desconocido";
                        String savedFilePath = fileStorageService.save(bytes, fileName);
                        String fileType = filePart.headers().getContentType() != null
                            ? filePart.headers().getContentType().toString()
                            : "application/octet-stream";

                        String sql =
                            "INSERT INTO file_record (file_name, file_path, file_type, change_request_id, upload_date) " +
                            "VALUES (:fileName, :filePath, :fileType, :reqId, :uploadDate)";

                        return databaseClient
                            .sql(sql)
                            .bind("fileName", fileName)
                            .bind("filePath", savedFilePath)
                            .bind("fileType", fileType)
                            .bind("reqId", id)
                            .bind("uploadDate", Instant.now())
                            .fetch()
                            .rowsUpdated()
                            // 👇 Llamada al Service para registrar en el historial 👇
                            .flatMap(rows -> changeRequestService.recordFileTracking(id, fileName))
                            .then(Mono.just(ResponseEntity.ok().<Void>build()));
                    });
            });
    }

    @GetMapping(value = "/archivo/{fileId}/descargar")
    public Mono<ResponseEntity<Resource>> downloadPhysicalFile(
        @PathVariable Long fileId,
        @RequestParam(value = "descargar", required = false, defaultValue = "false") boolean descargar
    ) {
        String sql = "SELECT file_name, file_path, file_type FROM file_record WHERE id = :id";
        return databaseClient
            .sql(sql)
            .bind("id", fileId)
            .map((row, metadata) ->
                new String[] { row.get("file_name", String.class), row.get("file_path", String.class), row.get("file_type", String.class) }
            )
            .first()
            .flatMap(data -> {
                try {
                    Path path = Paths.get(data[1]);
                    Resource resource = new UrlResource(path.toUri());
                    if (resource.exists() && resource.isReadable()) {
                        return Mono.just(
                            ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(data[2]))
                                .header(
                                    HttpHeaders.CONTENT_DISPOSITION,
                                    (descargar ? "attachment" : "inline") + "; filename=\"" + data[0] + "\""
                                )
                                .body(resource)
                        );
                    }
                } catch (MalformedURLException e) {
                    return Mono.error(e);
                }
                return Mono.just(ResponseEntity.notFound().build());
            });
    }

    @PostMapping("")
    public Mono<ResponseEntity<ChangeRequestDTO>> createChangeRequest(@Valid @RequestBody ChangeRequestDTO changeRequestDTO)
        throws URISyntaxException {
        if (changeRequestDTO.getId() != null) throw new BadRequestAlertException("A new idexists", ENTITY_NAME, "idexists");
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

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ChangeRequestDTO>> updateChangeRequest(
        @PathVariable Long id,
        @Valid @RequestBody ChangeRequestDTO changeRequestDTO
    ) {
        return changeRequestService
            .update(changeRequestDTO)
            .map(result ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                    .body(result)
            );
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ChangeRequestDTO>> partialUpdateChangeRequest(
        @PathVariable Long id,
        @NotNull @RequestBody ChangeRequestDTO changeRequestDTO
    ) {
        return changeRequestService
            .partialUpdate(changeRequestDTO)
            .map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
    }

    @GetMapping("")
    public Mono<ResponseEntity<List<ChangeRequestDTO>>> getAllChangeRequests(
        @RequestParam(value = "globalSearch", defaultValue = "") String globalSearch,
        @RequestParam(value = "status.equals", defaultValue = "") String status,
        @RequestParam(value = "createdDate.greaterThanOrEqual", required = false) String start,
        @RequestParam(value = "createdDate.lessThanOrEqual", required = false) String end,
        Pageable pageable,
        ServerHttpRequest request
    ) {
        Instant startDate = start != null ? Instant.parse(start) : Instant.parse("1970-01-01T00:00:00Z");
        Instant endDate = end != null ? Instant.parse(end) : Instant.parse("2099-12-31T23:59:59Z");
        return changeRequestService
            .countByFilters(globalSearch, status, startDate, endDate)
            .zipWith(changeRequestService.findByFilters(globalSearch, status, startDate, endDate, pageable).collectList())
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
    public Mono<ResponseEntity<ChangeRequestDTO>> getChangeRequest(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(changeRequestService.findOne(id));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteChangeRequest(@PathVariable Long id) {
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
