package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.repository.rowmapper.TrackingRecordRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the TrackingRecord entity.
 */
@SuppressWarnings("unused")
class TrackingRecordRepositoryInternalImpl implements TrackingRecordRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrackingRecordRowMapper trackingRecordRowMapper;

    private static final Table entityTable = Table.aliased("tracking_record", EntityManager.ENTITY_ALIAS);

    public TrackingRecordRepositoryInternalImpl(
        R2dbcEntityTemplate r2dbcEntityTemplate,
        EntityManager entityManager,
        TrackingRecordRowMapper trackingRecordRowMapper,
        DatabaseClient db
    ) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.entityManager = entityManager;
        this.trackingRecordRowMapper = trackingRecordRowMapper;
        this.db = db;
    }

    @Override
    public Flux<TrackingRecord> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<TrackingRecord> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<TrackingRecord> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = TrackingRecordSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        // Aquí el truco: Si criteria es null, pasamos null.
        // Si no es null, deberíamos convertirlo, pero para que compile rápido:
        Condition where = null;

        // El EntityManager que arreglamos antes soporta esto:
        String select = entityManager.createSelect(selectFrom, TrackingRecord.class, pageable, where);

        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrackingRecord> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<TrackingRecord> findById(Long id) {
        Condition where = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));

        List<Expression> columns = TrackingRecordSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, TrackingRecord.class, null, where);

        return db.sql(select).map(this::process).one();
    }

    @Override
    public Mono<TrackingRecord> findById(Integer id) {
        return findById(Long.valueOf(id));
    }

    private TrackingRecord process(Row row, RowMetadata metadata) {
        return trackingRecordRowMapper.apply(row, "e_");
    }
}
