package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrackingRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
@Repository
public interface TrackingRecordRepository extends ReactiveCrudRepository<TrackingRecord, Long>, TrackingRecordRepositoryInternal {
    Flux<TrackingRecord> findAllBy(Pageable pageable);

    @Query("SELECT * FROM tracking_record WHERE user_id = :id")
    Flux<TrackingRecord> findByUser(Long id);

    @Query("SELECT * FROM tracking_record WHERE user_id IS NULL")
    Flux<TrackingRecord> findAllWhereUserIsNull();

    Flux<TrackingRecord> findByChangeRequestId(Long id);

    //  CONSULTAS DE ESTADÍSTICAS MODIFICADAS PARA FILTROS (R2DBC)

    @Query(
        "SELECT tr.department_id AS id, dep.department_name AS nombre, CAST(COUNT(*) AS int) AS total " +
        "FROM tracking_record tr " +
        "JOIN department dep ON dep.id = tr.department_id " +
        "WHERE (:year IS NULL OR EXTRACT(YEAR FROM tr.change_date) = :year) " +
        "AND (:month IS NULL OR EXTRACT(MONTH FROM tr.change_date) = :month) " +
        "GROUP BY tr.department_id, dep.department_name ORDER BY total DESC"
    )
    Flux<TrackingStats> countMovementsByDepartment(Integer year, Integer month);

    @Query(
        "SELECT tr.user_id AS id, u.login AS nombre, CAST(COUNT(*) AS int) AS total " +
        "FROM tracking_record tr " +
        "JOIN jhi_user u ON u.id = tr.user_id " +
        "WHERE (:year IS NULL OR EXTRACT(YEAR FROM tr.change_date) = :year) " +
        "AND (:month IS NULL OR EXTRACT(MONTH FROM tr.change_date) = :month) " +
        "GROUP BY tr.user_id, u.login ORDER BY total DESC"
    )
    Flux<TrackingStats> countMovementsByUser(Integer year, Integer month);

    //  CONSULTAS DE PRIVACIDAD PARA USUARIOS NORMALES

    @Query(
        "SELECT tr.* FROM tracking_record tr " +
        "INNER JOIN change_request cr ON tr.change_request_id = cr.id " +
        "WHERE cr.user_id = :userId " +
        "ORDER BY tr.id DESC " +
        "LIMIT :limit OFFSET :offset"
    )
    Flux<TrackingRecord> findAllByChangeRequestUser(Long userId, int limit, long offset);

    @Query(
        "SELECT COUNT(tr.id) FROM tracking_record tr " +
        "INNER JOIN change_request cr ON tr.change_request_id = cr.id " +
        "WHERE cr.user_id = :userId"
    )
    Mono<Long> countAllByChangeRequestUser(Long userId);

    @Override
    <S extends TrackingRecord> Mono<S> save(S entity);

    @Override
    Flux<TrackingRecord> findAll();

    @Override
    Mono<TrackingRecord> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}
