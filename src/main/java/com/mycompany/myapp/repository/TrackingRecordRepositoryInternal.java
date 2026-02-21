package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrackingRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public interface TrackingRecordRepositoryInternal {
    // Estos son los de JHipster por defecto
    Flux<TrackingRecord> findAllBy(Pageable pageable);

    Flux<TrackingRecord> findAllBy(Pageable pageable, Criteria criteria);

    Flux<TrackingRecord> findAll();

    Mono<TrackingRecord> findById(Long id);

    // 👇 AQUI ESTA NUESTRA PROMESA DEL METODO NUEVO 👇
    Flux<TrackingRecord> findByChangeRequestId(Long id);
}
