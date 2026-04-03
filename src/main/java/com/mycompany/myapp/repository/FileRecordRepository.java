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

    Flux<FileRecord> findByChangeRequestId(Long changeRequestId, Pageable pageable);

    // --- Para contar el total de registros filtrados (vital para la paginación en Angular) ---
    Mono<Long> countByChangeRequestId(Long changeRequestId);

    @Query("SELECT * FROM file_record entity WHERE entity.change_request_id IS NULL")
    Flux<FileRecord> findAllWhereChangeRequestIsNull();

    //   CONSULTAS DE PRIVACIDAD PARA USUARIOS NORMALES

    // Trae solo los archivos vinculados a las solicitudes de este usuario (Paginado)
    @Query(
        "SELECT fr.* FROM file_record fr " +
        "INNER JOIN change_request cr ON fr.change_request_id = cr.id " +
        "WHERE cr.user_id = :userId " +
        "ORDER BY fr.id DESC " +
        "LIMIT :limit OFFSET :offset"
    )
    Flux<FileRecord> findAllByChangeRequestUser(Long userId, int limit, long offset);

    // Cuenta el total de archivos del usuario para la paginación
    @Query(
        "SELECT COUNT(fr.id) FROM file_record fr " +
        "INNER JOIN change_request cr ON fr.change_request_id = cr.id " +
        "WHERE cr.user_id = :userId"
    )
    Mono<Long> countAllByChangeRequestUser(Long userId);

    // ========================================================================

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

    Flux<FileRecord> findByChangeRequestUserId(Long userId, Pageable pageable);

    Flux<FileRecord> findAll();

    Mono<FileRecord> findById(Long id);
}
