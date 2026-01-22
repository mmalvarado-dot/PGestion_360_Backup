package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ItemCatalogue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ItemCatalogue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ItemCatalogueRepository extends ReactiveCrudRepository<ItemCatalogue, Long>, ItemCatalogueRepositoryInternal {
    Flux<ItemCatalogue> findAllBy(Pageable pageable);

    @Query("SELECT * FROM item_catalogue entity WHERE entity.catalogue_id = :id")
    Flux<ItemCatalogue> findByCatalogue(Long id);

    @Query("SELECT * FROM item_catalogue entity WHERE entity.catalogue_id IS NULL")
    Flux<ItemCatalogue> findAllWhereCatalogueIsNull();

    @Override
    <S extends ItemCatalogue> Mono<S> save(S entity);

    @Override
    Flux<ItemCatalogue> findAll();

    @Override
    Mono<ItemCatalogue> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ItemCatalogueRepositoryInternal {
    <S extends ItemCatalogue> Mono<S> save(S entity);

    Flux<ItemCatalogue> findAllBy(Pageable pageable);

    Flux<ItemCatalogue> findAll();

    Mono<ItemCatalogue> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ItemCatalogue> findAllBy(Pageable pageable, Criteria criteria);
}
