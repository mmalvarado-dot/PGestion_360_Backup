package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrackingRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrackingRecordRepositoryInternal {
    Flux<TrackingRecord> findAllBy(Pageable pageable);

    Flux<TrackingRecord> findAllBy(Pageable pageable, Criteria criteria);

    Flux<TrackingRecord> findAll();

    Mono<TrackingRecord> findById(Long id);

    Mono<TrackingRecord> findById(Integer id);
}
