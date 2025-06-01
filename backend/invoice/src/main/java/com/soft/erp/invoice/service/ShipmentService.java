package com.soft.erp.invoice.service;

import com.soft.erp.invoice.service.dto.ShipmentDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.soft.erp.invoice.domain.Shipment}.
 */
public interface ShipmentService {
    /**
     * Save a shipment.
     *
     * @param shipmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ShipmentDTO> save(ShipmentDTO shipmentDTO);

    /**
     * Updates a shipment.
     *
     * @param shipmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ShipmentDTO> update(ShipmentDTO shipmentDTO);

    /**
     * Partially updates a shipment.
     *
     * @param shipmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ShipmentDTO> partialUpdate(ShipmentDTO shipmentDTO);

    /**
     * Get all the shipments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ShipmentDTO> findAll(Pageable pageable);

    /**
     * Get all the shipments with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ShipmentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of shipments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" shipment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ShipmentDTO> findOne(Long id);

    /**
     * Delete the "id" shipment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
