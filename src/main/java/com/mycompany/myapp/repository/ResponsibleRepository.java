package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Responsible;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Responsible entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResponsibleRepository extends ReactiveCrudRepository<Responsible, Long>, ResponsibleRepositoryInternal {
    Flux<Responsible> findAllBy(Pageable pageable);

    @Override
    <S extends Responsible> Mono<S> save(S entity);

    @Override
    Flux<Responsible> findAll();

    @Override
    Mono<Responsible> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ResponsibleRepositoryInternal {
    <S extends Responsible> Mono<S> save(S entity);

    Flux<Responsible> findAllBy(Pageable pageable);

    Flux<Responsible> findAll();

    Mono<Responsible> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Responsible> findAllBy(Pageable pageable, Criteria criteria);
}
