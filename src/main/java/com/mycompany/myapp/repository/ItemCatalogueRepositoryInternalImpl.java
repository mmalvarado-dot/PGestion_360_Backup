package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ItemCatalogue;
import com.mycompany.myapp.repository.rowmapper.CatalogueRowMapper;
import com.mycompany.myapp.repository.rowmapper.ItemCatalogueRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ItemCatalogue entity.
 */
@SuppressWarnings("unused")
class ItemCatalogueRepositoryInternalImpl extends SimpleR2dbcRepository<ItemCatalogue, Long> implements ItemCatalogueRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CatalogueRowMapper catalogueMapper;
    private final ItemCatalogueRowMapper itemcatalogueMapper;

    private static final Table entityTable = Table.aliased("item_catalogue", EntityManager.ENTITY_ALIAS);
    private static final Table catalogueTable = Table.aliased("catalogue", "catalogue");

    public ItemCatalogueRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CatalogueRowMapper catalogueMapper,
        ItemCatalogueRowMapper itemcatalogueMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ItemCatalogue.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.catalogueMapper = catalogueMapper;
        this.itemcatalogueMapper = itemcatalogueMapper;
    }

    @Override
    public Flux<ItemCatalogue> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ItemCatalogue> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ItemCatalogueSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CatalogueSqlHelper.getColumns(catalogueTable, "catalogue"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(catalogueTable)
            .on(Column.create("catalogue_id", entityTable))
            .equals(Column.create("id", catalogueTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ItemCatalogue.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ItemCatalogue> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ItemCatalogue> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ItemCatalogue process(Row row, RowMetadata metadata) {
        ItemCatalogue entity = itemcatalogueMapper.apply(row, "e");
        entity.setCatalogue(catalogueMapper.apply(row, "catalogue"));
        return entity;
    }

    @Override
    public <S extends ItemCatalogue> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
