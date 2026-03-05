package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrackingRecord;
// 1. IMPORTAMOS EL MAPPER DE LA SOLICITUD
import com.mycompany.myapp.repository.rowmapper.ChangeRequestRowMapper;
import com.mycompany.myapp.repository.rowmapper.DepartmentRowMapper;
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
    private final DepartmentRowMapper departmentMapper;
    // 2. DECLARAMOS EL MAPPER DE LA SOLICITUD
    private final ChangeRequestRowMapper changeRequestMapper;

    private static final Table entityTable = Table.aliased("tracking_record", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "jhiUser");
    private static final Table departmentTable = Table.aliased("department", "department");
    // 3. DECLARAMOS LA TABLA DE LA SOLICITUD
    private static final Table changeRequestTable = Table.aliased("change_request", "changeRequest");

    public TrackingRecordRepositoryInternalImpl(
        R2dbcEntityTemplate r2dbcEntityTemplate,
        EntityManager entityManager,
        TrackingRecordRowMapper trackingRecordRowMapper,
        UserRowMapper userMapper,
        DepartmentRowMapper departmentMapper,
        ChangeRequestRowMapper changeRequestMapper, // 4. LO AGREGAMOS AL CONSTRUCTOR
        DatabaseClient db
    ) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.entityManager = entityManager;
        this.trackingRecordRowMapper = trackingRecordRowMapper;
        this.userMapper = userMapper;
        this.departmentMapper = departmentMapper;
        this.changeRequestMapper = changeRequestMapper; // 5. LO INICIALIZAMOS
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
        // --- INICIO DE LA CORRECCIÓN DE ORDENAMIENTO ---
        if (pageable != null && pageable.getSort().isSorted()) {
            org.springframework.data.domain.Sort newSort = org.springframework.data.domain.Sort.by(
                pageable
                    .getSort()
                    .stream()
                    .map(order -> {
                        if ("changeDate".equals(order.getProperty())) {
                            return new org.springframework.data.domain.Sort.Order(order.getDirection(), "change_date");
                        }
                        return order;
                    })
                    .collect(java.util.stream.Collectors.toList())
            );
            pageable = org.springframework.data.domain.PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
        }
        // --- FIN DE LA CORRECCIÓN ---

        List<Expression> columns = TrackingRecordSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);

        columns.addAll(UserSqlHelper.getColumns(userTable, "jhiUser"));
        columns.addAll(DepartmentSqlHelper.getColumns(departmentTable, "department"));
        // 6. LE DECIMOS A SQL QUE TRAIGA LAS COLUMNAS DE LA SOLICITUD
        columns.addAll(ChangeRequestSqlHelper.getColumns(changeRequestTable, "changeRequest"));

        org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(departmentTable)
            .on(Column.create("department_id", entityTable))
            .equals(Column.create("id", departmentTable))
            // 7. HACEMOS EL JOIN (LA UNIÓN) EN LA BASE DE DATOS
            .leftOuterJoin(changeRequestTable)
            .on(Column.create("change_request_id", entityTable))
            .equals(Column.create("id", changeRequestTable));

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

        entity.setUser(userMapper.apply(row, "jhiUser"));
        entity.setDepartment(departmentMapper.apply(row, "department"));

        // 8. ¡ENSAMBLAMOS LA SOLICITUD AL FIN!
        entity.setChangeRequest(changeRequestMapper.apply(row, "changeRequest"));

        return entity;
    }
}
