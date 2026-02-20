package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrackingRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrackingRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrackingRecordRepository extends ReactiveCrudRepository<TrackingRecord, Long>, TrackingRecordRepositoryInternal {
    // ELIMINADO @Query: Al quitarlo, Spring usa TrackingRecordRepositoryInternalImpl.findAllBy
    // que es donde configuramos los JOINs y el mapeo manual de los objetos.
    Flux<TrackingRecord> findAllBy(Pageable pageable);

    // --- BÚSQUEDAS ESTÁNDAR ---

    @Query("SELECT * FROM tracking_record entity WHERE entity.user_id = :id")
    Flux<TrackingRecord> findByUser(Long id);

    @Query("SELECT * FROM tracking_record entity WHERE entity.user_id IS NULL")
    Flux<TrackingRecord> findAllWhereUserIsNull();

    @Query("SELECT * FROM tracking_record entity WHERE entity.responsible_id = :id")
    Flux<TrackingRecord> findByResponsible(Long id);

    // --- BÚSQUEDAS PARA EL HISTORIAL ---

    // También quitamos el @Query aquí para que use la implementación interna con mapeo
    Flux<TrackingRecord> findByChangeRequestId(Long id);

    @Query("SELECT * FROM tracking_record entity WHERE entity.department_id = :id ORDER BY change_date DESC")
    Flux<TrackingRecord> findByDepartmentId(Long id);

    // --- CONSULTAS PARA ESTADÍSTICAS ---

    @Query(
        "SELECT tr.department_id AS id, dep.department_name AS nombre, CAST(COUNT(*) AS int) AS total " +
        "FROM tracking_record tr " +
        "JOIN department dep ON dep.id = tr.department_id " +
        "GROUP BY tr.department_id, dep.department_name ORDER BY total DESC"
    )
    Flux<TrackingStats> countMovementsByDepartment();

    @Query(
        "SELECT tr.responsible_id AS id, res.name AS nombre, CAST(COUNT(*) AS int) AS total " +
        "FROM tracking_record tr " +
        "JOIN responsible res ON res.id = tr.responsible_id " +
        "GROUP BY tr.responsible_id, res.name ORDER BY total DESC"
    )
    Flux<TrackingStats> countMovementsByResponsible();

    // -------------------------------------------------------------------------

    @Override
    <S extends TrackingRecord> Mono<S> save(S entity);

    @Override
    Flux<TrackingRecord> findAll();

    @Override
    Mono<TrackingRecord> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrackingRecordRepositoryInternal {
    <S extends TrackingRecord> Mono<S> save(S entity);
    Flux<TrackingRecord> findAllBy(Pageable pageable);
    Flux<TrackingRecord> findAll();
    Mono<TrackingRecord> findById(Long id);
    // Añadimos esto para que el historial también use el mapeo manual
    Flux<TrackingRecord> findByChangeRequestId(Long id);
}
