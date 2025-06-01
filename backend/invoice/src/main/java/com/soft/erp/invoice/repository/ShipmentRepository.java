package com.soft.erp.invoice.repository;

import com.soft.erp.invoice.domain.Shipment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Shipment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentRepository extends ReactiveCrudRepository<Shipment, Long>, ShipmentRepositoryInternal {
    Flux<Shipment> findAllBy(Pageable pageable);

    @Override
    Mono<Shipment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Shipment> findAllWithEagerRelationships();

    @Override
    Flux<Shipment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM shipment entity WHERE entity.invoice_id = :id")
    Flux<Shipment> findByInvoice(Long id);

    @Query("SELECT * FROM shipment entity WHERE entity.invoice_id IS NULL")
    Flux<Shipment> findAllWhereInvoiceIsNull();

    @Override
    <S extends Shipment> Mono<S> save(S entity);

    @Override
    Flux<Shipment> findAll();

    @Override
    Mono<Shipment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ShipmentRepositoryInternal {
    <S extends Shipment> Mono<S> save(S entity);

    Flux<Shipment> findAllBy(Pageable pageable);

    Flux<Shipment> findAll();

    Mono<Shipment> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Shipment> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Shipment> findOneWithEagerRelationships(Long id);

    Flux<Shipment> findAllWithEagerRelationships();

    Flux<Shipment> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
