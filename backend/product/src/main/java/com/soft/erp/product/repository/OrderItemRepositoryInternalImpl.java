package com.soft.erp.product.repository;

import com.soft.erp.product.domain.OrderItem;
import com.soft.erp.product.repository.rowmapper.OrderItemRowMapper;
import com.soft.erp.product.repository.rowmapper.ProductOrderRowMapper;
import com.soft.erp.product.repository.rowmapper.ProductRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the OrderItem entity.
 */
@SuppressWarnings("unused")
class OrderItemRepositoryInternalImpl extends SimpleR2dbcRepository<OrderItem, Long> implements OrderItemRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProductRowMapper productMapper;
    private final ProductOrderRowMapper productorderMapper;
    private final OrderItemRowMapper orderitemMapper;

    private static final Table entityTable = Table.aliased("order_item", EntityManager.ENTITY_ALIAS);
    private static final Table productTable = Table.aliased("product", "product");
    private static final Table orderTable = Table.aliased("product_order", "e_order");

    public OrderItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProductRowMapper productMapper,
        ProductOrderRowMapper productorderMapper,
        OrderItemRowMapper orderitemMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(OrderItem.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.productMapper = productMapper;
        this.productorderMapper = productorderMapper;
        this.orderitemMapper = orderitemMapper;
    }

    @Override
    public Flux<OrderItem> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<OrderItem> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OrderItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        columns.addAll(ProductOrderSqlHelper.getColumns(orderTable, "order"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable))
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, OrderItem.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<OrderItem> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<OrderItem> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<OrderItem> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<OrderItem> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<OrderItem> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private OrderItem process(Row row, RowMetadata metadata) {
        OrderItem entity = orderitemMapper.apply(row, "e");
        entity.setProduct(productMapper.apply(row, "product"));
        entity.setOrder(productorderMapper.apply(row, "order"));
        return entity;
    }

    @Override
    public <S extends OrderItem> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
