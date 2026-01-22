package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Catalogue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Catalogue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CatalogueRepository extends ReactiveCrudRepository<Catalogue, Long>, CatalogueRepositoryInternal {
    Flux<Catalogue> findAllBy(Pageable pageable);

    @Override
    <S extends Catalogue> Mono<S> save(S entity);

    @Override
    Flux<Catalogue> findAll();

    @Override
    Mono<Catalogue> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CatalogueRepositoryInternal {
    <S extends Catalogue> Mono<S> save(S entity);

    Flux<Catalogue> findAllBy(Pageable pageable);

    Flux<Catalogue> findAll();

    Mono<Catalogue> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Catalogue> findAllBy(Pageable pageable, Criteria criteria);
}
