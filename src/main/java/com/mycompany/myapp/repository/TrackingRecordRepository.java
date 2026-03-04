package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrackingRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TrackingRecordRepository extends ReactiveCrudRepository<TrackingRecord, Long>, TrackingRecordRepositoryInternal {
    Flux<TrackingRecord> findAllBy(Pageable pageable);

    @Query("SELECT * FROM tracking_record entity WHERE entity.user_id = :id")
    Flux<TrackingRecord> findByUser(Long id);

    @Query("SELECT * FROM tracking_record entity WHERE entity.user_id IS NULL")
    Flux<TrackingRecord> findAllWhereUserIsNull();

    Flux<TrackingRecord> findByChangeRequestId(Long id);

    @Query(
        "SELECT tr.department_id AS id, dep.department_name AS nombre, CAST(COUNT(*) AS int) AS total " +
        "FROM tracking_record tr " +
        "JOIN department dep ON dep.id = tr.department_id " +
        "GROUP BY tr.department_id, dep.department_name ORDER BY total DESC"
    )
    Flux<TrackingStats> countMovementsByDepartment();

    @Query(
        "SELECT tr.user_id AS id, u.login AS nombre, CAST(COUNT(*) AS int) AS total " +
        "FROM tracking_record tr " +
        "JOIN jhi_user u ON u.id = tr.user_id " +
        "GROUP BY tr.user_id, u.login ORDER BY total DESC"
    )
    Flux<TrackingStats> countMovementsByUser();

    @Override
    <S extends TrackingRecord> Mono<S> save(S entity);

    @Override
    Flux<TrackingRecord> findAll();

    @Override
    Mono<TrackingRecord> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}
