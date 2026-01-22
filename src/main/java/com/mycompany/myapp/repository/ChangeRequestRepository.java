package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ChangeRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ChangeRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChangeRequestRepository extends ReactiveCrudRepository<ChangeRequest, Long>, ChangeRequestRepositoryInternal {
    Flux<ChangeRequest> findAllBy(Pageable pageable);

    @Query("SELECT * FROM change_request entity WHERE entity.responsible_id = :id")
    Flux<ChangeRequest> findByResponsible(Long id);

    @Query("SELECT * FROM change_request entity WHERE entity.responsible_id IS NULL")
    Flux<ChangeRequest> findAllWhereResponsibleIsNull();

    @Query("SELECT * FROM change_request entity WHERE entity.item_catalogue_id = :id")
    Flux<ChangeRequest> findByItemCatalogue(Long id);

    @Query("SELECT * FROM change_request entity WHERE entity.item_catalogue_id IS NULL")
    Flux<ChangeRequest> findAllWhereItemCatalogueIsNull();

    @Override
    <S extends ChangeRequest> Mono<S> save(S entity);

    @Override
    Flux<ChangeRequest> findAll();

    @Override
    Mono<ChangeRequest> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ChangeRequestRepositoryInternal {
    <S extends ChangeRequest> Mono<S> save(S entity);

    Flux<ChangeRequest> findAllBy(Pageable pageable);

    Flux<ChangeRequest> findAll();

    Mono<ChangeRequest> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ChangeRequest> findAllBy(Pageable pageable, Criteria criteria);
}
