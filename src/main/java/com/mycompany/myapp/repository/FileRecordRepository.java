package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.FileRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the FileRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileRecordRepository extends ReactiveCrudRepository<FileRecord, Long>, FileRecordRepositoryInternal {
    Flux<FileRecord> findAllBy(Pageable pageable);

    @Query("SELECT * FROM file_record entity WHERE entity.change_request_id = :id")
    Flux<FileRecord> findByChangeRequest(Long id);

    // --- NUEVO: Para buscar con paginación por ID de solicitud ---
    Flux<FileRecord> findByChangeRequestId(Long changeRequestId, Pageable pageable);

    // --- NUEVO: Para contar el total de registros filtrados (vital para la paginación en Angular) ---
    Mono<Long> countByChangeRequestId(Long changeRequestId);

    // -----------------------------------------------------------------------------------------------

    @Query("SELECT * FROM file_record entity WHERE entity.change_request_id IS NULL")
    Flux<FileRecord> findAllWhereChangeRequestIsNull();

    @Override
    <S extends FileRecord> Mono<S> save(S entity);

    @Override
    Flux<FileRecord> findAll();

    @Override
    Mono<FileRecord> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FileRecordRepositoryInternal {
    <S extends FileRecord> Mono<S> save(S entity);

    Flux<FileRecord> findAllBy(Pageable pageable);

    Flux<FileRecord> findAll();

    Mono<FileRecord> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<FileRecord> findAllBy(Pageable pageable, Criteria criteria);
}
