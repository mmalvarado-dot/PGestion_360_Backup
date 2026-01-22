package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.FileRecord;
import com.mycompany.myapp.repository.rowmapper.ChangeRequestRowMapper;
import com.mycompany.myapp.repository.rowmapper.FileRecordRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the FileRecord entity.
 */
@SuppressWarnings("unused")
class FileRecordRepositoryInternalImpl extends SimpleR2dbcRepository<FileRecord, Long> implements FileRecordRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ChangeRequestRowMapper changerequestMapper;
    private final FileRecordRowMapper filerecordMapper;

    private static final Table entityTable = Table.aliased("file_record", EntityManager.ENTITY_ALIAS);
    private static final Table changeRequestTable = Table.aliased("change_request", "changeRequest");

    public FileRecordRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ChangeRequestRowMapper changerequestMapper,
        FileRecordRowMapper filerecordMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(FileRecord.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.changerequestMapper = changerequestMapper;
        this.filerecordMapper = filerecordMapper;
    }

    @Override
    public Flux<FileRecord> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<FileRecord> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = FileRecordSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ChangeRequestSqlHelper.getColumns(changeRequestTable, "changeRequest"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(changeRequestTable)
            .on(Column.create("change_request_id", entityTable))
            .equals(Column.create("id", changeRequestTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, FileRecord.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<FileRecord> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<FileRecord> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private FileRecord process(Row row, RowMetadata metadata) {
        FileRecord entity = filerecordMapper.apply(row, "e");
        entity.setChangeRequest(changerequestMapper.apply(row, "changeRequest"));
        return entity;
    }

    @Override
    public <S extends FileRecord> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
