package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.repository.rowmapper.ChangeRequestRowMapper;
import com.mycompany.myapp.repository.rowmapper.ResponsibleRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrackingRecordRowMapper;
import com.mycompany.myapp.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the TrackingRecord entity.
 */
@SuppressWarnings("unused")
class TrackingRecordRepositoryInternalImpl extends SimpleR2dbcRepository<TrackingRecord, Long> implements TrackingRecordRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final ResponsibleRowMapper responsibleMapper;
    private final ChangeRequestRowMapper changerequestMapper;
    private final TrackingRecordRowMapper trackingrecordMapper;

    private static final Table entityTable = Table.aliased("tracking_record", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table responsibleTable = Table.aliased("responsible", "responsible");
    private static final Table changeRequestTable = Table.aliased("change_request", "changeRequest");

    public TrackingRecordRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        ResponsibleRowMapper responsibleMapper,
        ChangeRequestRowMapper changerequestMapper,
        TrackingRecordRowMapper trackingrecordMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrackingRecord.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.responsibleMapper = responsibleMapper;
        this.changerequestMapper = changerequestMapper;
        this.trackingrecordMapper = trackingrecordMapper;
    }

    @Override
    public Flux<TrackingRecord> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrackingRecord> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrackingRecordSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(ResponsibleSqlHelper.getColumns(responsibleTable, "responsible"));
        columns.addAll(ChangeRequestSqlHelper.getColumns(changeRequestTable, "changeRequest"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(responsibleTable)
            .on(Column.create("responsible_id", entityTable))
            .equals(Column.create("id", responsibleTable))
            .leftOuterJoin(changeRequestTable)
            .on(Column.create("change_request_id", entityTable))
            .equals(Column.create("id", changeRequestTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrackingRecord.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrackingRecord> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrackingRecord> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrackingRecord process(Row row, RowMetadata metadata) {
        TrackingRecord entity = trackingrecordMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setResponsible(responsibleMapper.apply(row, "responsible"));
        entity.setChangeRequest(changerequestMapper.apply(row, "changeRequest"));
        return entity;
    }

    @Override
    public <S extends TrackingRecord> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
