package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.StatementMapper;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.OrderByField;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectOrdered;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.r2dbc.core.Parameter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class EntityManager {

    public static final String ENTITY_ALIAS = "e";
    public static final String ALIAS_PREFIX = "e_";

    public static class LinkTable {

        final String tableName;
        final String idColumn;
        final String referenceColumn;

        public LinkTable(String tableName, String idColumn, String referenceColumn) {
            Assert.notNull(tableName, "tableName is null");
            Assert.notNull(idColumn, "idColumn is null");
            Assert.notNull(referenceColumn, "referenceColumn is null");
            this.tableName = tableName;
            this.idColumn = idColumn;
            this.referenceColumn = referenceColumn;
        }
    }

    private final SqlRenderer sqlRenderer;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final StatementMapper statementMapper;

    public EntityManager(SqlRenderer sqlRenderer, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.sqlRenderer = sqlRenderer;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.statementMapper = r2dbcEntityTemplate.getDataAccessStrategy().getStatementMapper();
    }

    public String createSelect(SelectFromAndJoin selectFrom, Class<?> entityType, Pageable pageable, Condition where) {
        if (pageable != null) {
            if (where != null) {
                return createSelectImpl(
                    selectFrom.limitOffset(pageable.getPageSize(), pageable.getOffset()).where(where),
                    entityType,
                    pageable.getSort()
                );
            } else {
                return createSelectImpl(
                    selectFrom.limitOffset(pageable.getPageSize(), pageable.getOffset()),
                    entityType,
                    pageable.getSort()
                );
            }
        } else {
            if (where != null) {
                return createSelectImpl(selectFrom.where(where), entityType, null);
            } else {
                return createSelectImpl(selectFrom, entityType, null);
            }
        }
    }

    public String createSelect(SelectFromAndJoinCondition selectFrom, Class<?> entityType, Pageable pageable, Condition where) {
        if (pageable != null) {
            if (where != null) {
                return createSelectImpl(
                    selectFrom.limitOffset(pageable.getPageSize(), pageable.getOffset()).where(where),
                    entityType,
                    pageable.getSort()
                );
            } else {
                return createSelectImpl(
                    selectFrom.limitOffset(pageable.getPageSize(), pageable.getOffset()),
                    entityType,
                    pageable.getSort()
                );
            }
        } else {
            if (where != null) {
                return createSelectImpl(selectFrom.where(where), entityType, null);
            } else {
                return createSelectImpl(selectFrom, entityType, null);
            }
        }
    }

    public String createSelect(Select select) {
        return sqlRenderer.render(select);
    }

    public Mono<Long> deleteAll(Class<?> entityType) {
        return r2dbcEntityTemplate.delete(entityType).all();
    }

    public Mono<Long> deleteAll(String tableName) {
        StatementMapper.DeleteSpec delete = statementMapper.createDelete(tableName);
        return r2dbcEntityTemplate
            .getDatabaseClient()
            .sql(statementMapper.getMappedObject(delete))
            .fetch()
            .rowsUpdated()
            .map(count -> Long.valueOf(count));
    }

    public <S> Mono<S> insert(S entity) {
        return r2dbcEntityTemplate.insert(entity);
    }

    public Mono<Long> updateLinkTable(LinkTable table, Object entityId, Stream<?> referencedIds) {
        return deleteFromLinkTable(table, entityId).then(
            Flux.fromStream(referencedIds)
                .flatMap((Object referenceId) -> {
                    StatementMapper.InsertSpec insert = statementMapper
                        .createInsert(table.tableName)
                        .withColumn(table.idColumn, Parameter.from(entityId))
                        .withColumn(table.referenceColumn, Parameter.from(referenceId));

                    return r2dbcEntityTemplate
                        .getDatabaseClient()
                        .sql(statementMapper.getMappedObject(insert))
                        .fetch()
                        .rowsUpdated()
                        .map(count -> Long.valueOf(count));
                })
                .collectList()
                .map((List<Long> updates) -> updates.stream().reduce(Long::sum).orElse(0L))
        );
    }

    public Mono<Void> deleteFromLinkTable(LinkTable table, Object entityId) {
        Assert.notNull(entityId, "entityId is null");
        StatementMapper.DeleteSpec deleteSpec = statementMapper
            .createDelete(table.tableName)
            .withCriteria(Criteria.from(Criteria.where(table.idColumn).is(entityId)));
        return r2dbcEntityTemplate.getDatabaseClient().sql(statementMapper.getMappedObject(deleteSpec)).then();
    }

    private String createSelectImpl(SelectOrdered selectFrom, Class<?> entityType, Sort sortParameter) {
        if (sortParameter != null && sortParameter.isSorted()) {
            RelationalPersistentEntity<?> entity = getPersistentEntity(entityType);
            if (entity != null) {
                selectFrom = selectFrom.orderBy(
                    createOrderByFields(Table.create(entity.getTableName()).as(EntityManager.ENTITY_ALIAS), sortParameter)
                );
            }
        }
        return createSelect(selectFrom.build());
    }

    private RelationalPersistentEntity<?> getPersistentEntity(Class<?> entityType) {
        return r2dbcEntityTemplate.getConverter().getMappingContext().getPersistentEntity(entityType);
    }

    private static Collection<? extends OrderByField> createOrderByFields(Table table, Sort sortToUse) {
        List<OrderByField> fields = new ArrayList<>();

        for (Sort.Order order : sortToUse) {
            String propertyName = order.getProperty();
            OrderByField orderByField = !propertyName.contains(".")
                ? OrderByField.from(table.column(propertyName).as(EntityManager.ALIAS_PREFIX + propertyName))
                : createOrderByField(propertyName);

            fields.add(order.isAscending() ? orderByField.asc() : orderByField.desc());
        }

        return fields;
    }

    private static OrderByField createOrderByField(String propertyName) {
        String[] parts = propertyName.split("\\.");
        String tableName = parts[0];
        String columnName = parts[1];

        return OrderByField.from(
            Column.aliased(
                columnName,
                Table.aliased(camelCaseToSnakeCase(tableName), tableName),
                String.format("%s_%s", tableName, columnName)
            )
        );
    }

    public static String camelCaseToSnakeCase(String input) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return input.replaceAll(regex, replacement).toLowerCase();
    }
}
