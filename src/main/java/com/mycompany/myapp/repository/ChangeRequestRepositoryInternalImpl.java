package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.repository.rowmapper.ChangeRequestRowMapper;
import com.mycompany.myapp.repository.rowmapper.ItemCatalogueRowMapper;
import com.mycompany.myapp.repository.rowmapper.ResponsibleRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ChangeRequest entity.
 */
@SuppressWarnings("unused")
class ChangeRequestRepositoryInternalImpl extends SimpleR2dbcRepository<ChangeRequest, Long> implements ChangeRequestRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ResponsibleRowMapper responsibleMapper;
    private final ItemCatalogueRowMapper itemcatalogueMapper;
    private final ChangeRequestRowMapper changerequestMapper;

    private static final Table entityTable = Table.aliased("change_request", EntityManager.ENTITY_ALIAS);
    private static final Table responsibleTable = Table.aliased("responsible", "responsible");
    private static final Table itemCatalogueTable = Table.aliased("item_catalogue", "itemCatalogue");

    public ChangeRequestRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ResponsibleRowMapper responsibleMapper,
        ItemCatalogueRowMapper itemcatalogueMapper,
        ChangeRequestRowMapper changerequestMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ChangeRequest.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.responsibleMapper = responsibleMapper;
        this.itemcatalogueMapper = itemcatalogueMapper;
        this.changerequestMapper = changerequestMapper;
    }

    @Override
    public Flux<ChangeRequest> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ChangeRequest> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ChangeRequestSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ResponsibleSqlHelper.getColumns(responsibleTable, "responsible"));
        columns.addAll(ItemCatalogueSqlHelper.getColumns(itemCatalogueTable, "itemCatalogue"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(responsibleTable)
            .on(Column.create("responsible_id", entityTable))
            .equals(Column.create("id", responsibleTable))
            .leftOuterJoin(itemCatalogueTable)
            .on(Column.create("item_catalogue_id", entityTable))
            .equals(Column.create("id", itemCatalogueTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ChangeRequest.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ChangeRequest> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ChangeRequest> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ChangeRequest process(Row row, RowMetadata metadata) {
        ChangeRequest entity = changerequestMapper.apply(row, "e");
        entity.setResponsible(responsibleMapper.apply(row, "responsible"));
        entity.setItemCatalogue(itemcatalogueMapper.apply(row, "itemCatalogue"));
        return entity;
    }

    @Override
    public <S extends ChangeRequest> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
