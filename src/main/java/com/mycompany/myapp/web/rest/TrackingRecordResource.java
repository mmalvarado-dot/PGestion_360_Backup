package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.repository.TrackingStats;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.FileStorageService;
import com.mycompany.myapp.service.TrackingRecordService;
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.r2dbc.core.DatabaseClient;
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
    private final UserRepository userRepository;

    //  DEPENDENCIAS PARA MANEJO DE ARCHIVOS
    private final FileStorageService fileStorageService;
    private final DatabaseClient databaseClient;

    public TrackingRecordResource(
        TrackingRecordService trackingRecordService,
        TrackingRecordRepository trackingRecordRepository,
        UserRepository userRepository,
        FileStorageService fileStorageService,
        DatabaseClient databaseClient
    ) {
        this.trackingRecordService = trackingRecordService;
        this.trackingRecordRepository = trackingRecordRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.databaseClient = databaseClient;
    }

    //  ENDPOINT PARA RECIBIR LOS ARCHIVOS DEL AVANCE
    @PostMapping(value = "/{id}/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> uploadFile(@PathVariable Long id, @RequestPart("file") FilePart filePart) {
        LOG.debug("REST request para subir un archivo para TrackingRecord ID: {}", id);

        // 1. Primero buscamos el ID de la Solicitud (change_request_id) asociada a este Avance
        return databaseClient
            .sql("SELECT change_request_id FROM tracking_record WHERE id = :id")
            .bind("id", id)
            .map(row -> row.get("change_request_id", Long.class))
            .first()
            .switchIfEmpty(Mono.error(new BadRequestAlertException("TrackingRecord no encontrado", ENTITY_NAME, "idnotfound")))
            .flatMap(changeRequestId -> {
                // 2. Procesamos el archivo
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

                        // 3. ¡LA MAGIA! Insertamos el archivo vinculándolo al Avance Y a la Solicitud
                        String sql =
                            "INSERT INTO file_record (file_name, file_path, file_type, tracking_record_id, change_request_id, upload_date) " +
                            "VALUES (:fileName, :filePath, :fileType, :trkId, :reqId, :uploadDate)";

                        return databaseClient
                            .sql(sql)
                            .bind("fileName", fileName)
                            .bind("filePath", savedFilePath)
                            .bind("fileType", fileType)
                            .bind("trkId", id)
                            .bind("reqId", changeRequestId)
                            .bind("uploadDate", Instant.now())
                            .fetch()
                            .rowsUpdated()
                            .then(Mono.just(ResponseEntity.ok().<Void>build()));
                    });
            });
    }

    //  FIN DEL ENDPOINT DE ARCHIVOS

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
        LOG.debug("REST request to get a page of TrackingRecords");

        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
            .flatMap(isAdmin -> {
                if (Boolean.TRUE.equals(isAdmin)) {
                    return trackingRecordService.countAll().zipWith(trackingRecordService.findAll(pageable).collectList());
                } else {
                    return SecurityUtils.getCurrentUserLogin()
                        .flatMap(userRepository::findOneByLogin)
                        .flatMap(user ->
                            trackingRecordService
                                .countAllByUser(user.getId())
                                .zipWith(trackingRecordService.findAllByUser(pageable, user.getId()).collectList())
                        );
                }
            })
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

    //   ENDPOINTS DE ESTADÍSTICAS CON FILTROS DE FECHA

    @GetMapping(value = "/stats/departments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TrackingStats> getDepartmentStats(
        @RequestParam(value = "year", required = false) Integer year,
        @RequestParam(value = "month", required = false) Integer month
    ) {
        return trackingRecordService.getDepartmentStats(year, month);
    }

    @GetMapping(value = "/stats/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TrackingStats> getUserStats(
        @RequestParam(value = "year", required = false) Integer year,
        @RequestParam(value = "month", required = false) Integer month
    ) {
        return trackingRecordService.getUserStats(year, month);
    }
}
