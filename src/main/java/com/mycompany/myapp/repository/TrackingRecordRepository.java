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
    Flux<TrackingRecord> findAllBy(Pageable pageable);

    @Query("SELECT * FROM tracking_record entity WHERE entity.user_id = :id")
    Flux<TrackingRecord> findByUser(Long id);

    @Query("SELECT * FROM tracking_record entity WHERE entity.user_id IS NULL")
    Flux<TrackingRecord> findAllWhereUserIsNull();

    @Query("SELECT * FROM tracking_record entity WHERE entity.responsible_id = :id")
    Flux<TrackingRecord> findByResponsible(Long id);

    @Query("SELECT * FROM tracking_record entity WHERE entity.responsible_id IS NULL")
    Flux<TrackingRecord> findAllWhereResponsibleIsNull();

    @Query("SELECT * FROM tracking_record entity WHERE entity.change_request_id = :id")
    Flux<TrackingRecord> findByChangeRequest(Long id);

    @Query("SELECT * FROM tracking_record entity WHERE entity.change_request_id IS NULL")
    Flux<TrackingRecord> findAllWhereChangeRequestIsNull();

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
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrackingRecord> findAllBy(Pageable pageable, Criteria criteria);
}
