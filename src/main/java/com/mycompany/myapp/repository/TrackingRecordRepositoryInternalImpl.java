package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.repository.rowmapper.DepartmentRowMapper;
import com.mycompany.myapp.repository.rowmapper.ResponsibleRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrackingRecordRowMapper;
import com.mycompany.myapp.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
class TrackingRecordRepositoryInternalImpl implements TrackingRecordRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;
    private final TrackingRecordRowMapper trackingRecordRowMapper;
    private final UserRowMapper userMapper;
    private final ResponsibleRowMapper responsibleMapper;
    private final DepartmentRowMapper departmentMapper;

    private static final Table entityTable = Table.aliased("tracking_record", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "jhiUser");
    private static final Table responsibleTable = Table.aliased("responsible", "responsible");
    private static final Table departmentTable = Table.aliased("department", "department");

    public TrackingRecordRepositoryInternalImpl(
        R2dbcEntityTemplate r2dbcEntityTemplate,
        EntityManager entityManager,
        TrackingRecordRowMapper trackingRecordRowMapper,
        UserRowMapper userMapper,
        ResponsibleRowMapper responsibleMapper,
        DepartmentRowMapper departmentMapper,
        DatabaseClient db
    ) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.entityManager = entityManager;
        this.trackingRecordRowMapper = trackingRecordRowMapper;
        this.userMapper = userMapper;
        this.responsibleMapper = responsibleMapper;
        this.departmentMapper = departmentMapper;
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

        // Agregamos las columnas de las otras tablas
        columns.addAll(UserSqlHelper.getColumns(userTable, "jhiUser"));
        columns.addAll(ResponsibleSqlHelper.getColumns(responsibleTable, "responsible"));
        columns.addAll(DepartmentSqlHelper.getColumns(departmentTable, "department"));

        // AQUÍ ESTÁ LA MAGIA: Declaramos el tipo correcto (SelectFromAndJoinCondition) y encadenamos todo
        org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(responsibleTable)
            .on(Column.create("responsible_id", entityTable))
            .equals(Column.create("id", responsibleTable))
            .leftOuterJoin(departmentTable)
            .on(Column.create("department_id", entityTable))
            .equals(Column.create("id", departmentTable));

        Condition where = null;

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
        return createQuery(null, null).all().filter(record -> record.getId().equals(id)).next();
    }

    @Override
    public Flux<TrackingRecord> findByChangeRequestId(Long id) {
        Condition where = Conditions.isEqual(entityTable.column("change_request_id"), Conditions.just(id.toString()));
        return createQuery(null, null)
            .all()
            .filter(record -> record.getChangeRequestId() != null && record.getChangeRequestId().equals(id));
    }

    private TrackingRecord process(Row row, RowMetadata metadata) {
        TrackingRecord entity = trackingRecordRowMapper.apply(row, "e_");

        // Asignamos los objetos completos a la entidad
        entity.setUser(userMapper.apply(row, "jhiUser"));
        entity.setResponsible(responsibleMapper.apply(row, "responsible"));
        entity.setDepartment(departmentMapper.apply(row, "department"));

        return entity;
    }
}
