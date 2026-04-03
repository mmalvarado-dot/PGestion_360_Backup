package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ChangeRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
@Repository
public interface ChangeRequestRepository extends ReactiveCrudRepository<ChangeRequest, Long>, ChangeRequestRepositoryInternal {
    Flux<ChangeRequest> findAllBy(Pageable pageable);

    Mono<Long> countByUserId(Long userId);

    @Query("SELECT * FROM change_request WHERE user_id = :id")
    Flux<ChangeRequest> findByUser(Long id);

    @Query("SELECT * FROM change_request WHERE user_id IS NULL")
    Flux<ChangeRequest> findAllWhereUserIsNull();

    @Query("SELECT * FROM change_request WHERE item_catalogue_id = :id")
    Flux<ChangeRequest> findByItemCatalogue(Long id);

    @Query("SELECT * FROM change_request WHERE item_catalogue_id IS NULL")
    Flux<ChangeRequest> findAllWhereItemCatalogueIsNull();

    //   CONSULTAS PARA EL ADMIN (CON LEFT JOIN Y CAST )

    @Query(
        "SELECT c.* FROM change_request c " +
        "LEFT JOIN jhi_user u ON c.user_id = u.id " +
        "WHERE (:search IS NULL OR :search = '' " +
        "  OR CAST(c.id AS VARCHAR) ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR c.title ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR c.description ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR c.departamento ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR u.login ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR u.first_name ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR u.last_name ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        ") " +
        "AND (:status IS NULL OR :status = '' OR c.status = :status) " +
        "AND c.created_date >= :startDate " +
        "AND c.created_date <= :endDate " +
        "ORDER BY c.id ASC " +
        "LIMIT :limit OFFSET :offset"
    )
    Flux<ChangeRequest> findAllByFilters(
        String search,
        String status,
        java.time.Instant startDate,
        java.time.Instant endDate,
        int limit,
        long offset
    );

    @Query(
        "SELECT COUNT(c.id) FROM change_request c " +
        "LEFT JOIN jhi_user u ON c.user_id = u.id " +
        "WHERE (:search IS NULL OR :search = '' " +
        "  OR CAST(c.id AS VARCHAR) ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR c.title ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR c.description ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR c.departamento ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR u.login ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR u.first_name ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR u.last_name ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        ") " +
        "AND (:status IS NULL OR :status = '' OR c.status = :status) " +
        "AND c.created_date >= :startDate " +
        "AND c.created_date <= :endDate"
    )
    Mono<Long> countAllByFilters(String search, String status, java.time.Instant startDate, java.time.Instant endDate);

    //   CONSULTAS PARA USUARIOS NORMALES

    @Query(
        "SELECT * FROM change_request WHERE " +
        "user_id = :userId " +
        "AND (:search IS NULL OR :search = '' " +
        "  OR CAST(id AS VARCHAR) ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR title ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR description ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR departamento ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        ") " +
        "AND (:status IS NULL OR :status = '' OR status = :status) " +
        "AND created_date >= :startDate " +
        "AND created_date <= :endDate " +
        "ORDER BY id ASC " +
        "LIMIT :limit OFFSET :offset"
    )
    Flux<ChangeRequest> findByUserIdAndFilters(
        String search,
        String status,
        java.time.Instant startDate,
        java.time.Instant endDate,
        Long userId,
        int limit,
        long offset
    );

    @Query(
        "SELECT COUNT(id) FROM change_request WHERE " +
        "user_id = :userId " +
        "AND (:search IS NULL OR :search = '' " +
        "  OR CAST(id AS VARCHAR) ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR title ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR description ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        "  OR departamento ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') " +
        ") " +
        "AND (:status IS NULL OR :status = '' OR status = :status) " +
        "AND created_date >= :startDate " +
        "AND created_date <= :endDate"
    )
    Mono<Long> countByUserIdAndFilters(String search, String status, java.time.Instant startDate, java.time.Instant endDate, Long userId);

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
    Flux<ChangeRequest> findByUserId(Long userId, Pageable pageable);
    Flux<ChangeRequest> findAll();
    Mono<ChangeRequest> findById(Long id);
}
