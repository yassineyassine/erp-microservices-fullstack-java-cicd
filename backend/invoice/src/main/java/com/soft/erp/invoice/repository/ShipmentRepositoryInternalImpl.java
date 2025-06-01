package com.soft.erp.invoice.repository;

import com.soft.erp.invoice.domain.Shipment;
import com.soft.erp.invoice.repository.rowmapper.InvoiceRowMapper;
import com.soft.erp.invoice.repository.rowmapper.ShipmentRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Shipment entity.
 */
@SuppressWarnings("unused")
class ShipmentRepositoryInternalImpl extends SimpleR2dbcRepository<Shipment, Long> implements ShipmentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InvoiceRowMapper invoiceMapper;
    private final ShipmentRowMapper shipmentMapper;

    private static final Table entityTable = Table.aliased("shipment", EntityManager.ENTITY_ALIAS);
    private static final Table invoiceTable = Table.aliased("invoice", "invoice");

    public ShipmentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InvoiceRowMapper invoiceMapper,
        ShipmentRowMapper shipmentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Shipment.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.invoiceMapper = invoiceMapper;
        this.shipmentMapper = shipmentMapper;
    }

    @Override
    public Flux<Shipment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Shipment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ShipmentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InvoiceSqlHelper.getColumns(invoiceTable, "invoice"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(invoiceTable)
            .on(Column.create("invoice_id", entityTable))
            .equals(Column.create("id", invoiceTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Shipment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Shipment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Shipment> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Shipment> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Shipment> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Shipment> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Shipment process(Row row, RowMetadata metadata) {
        Shipment entity = shipmentMapper.apply(row, "e");
        entity.setInvoice(invoiceMapper.apply(row, "invoice"));
        return entity;
    }

    @Override
    public <S extends Shipment> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
