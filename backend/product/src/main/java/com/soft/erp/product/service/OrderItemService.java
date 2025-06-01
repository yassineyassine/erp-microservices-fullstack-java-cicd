package com.soft.erp.product.service;

import com.soft.erp.product.service.dto.OrderItemDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.soft.erp.product.domain.OrderItem}.
 */
public interface OrderItemService {
    /**
     * Save a orderItem.
     *
     * @param orderItemDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OrderItemDTO> save(OrderItemDTO orderItemDTO);

    /**
     * Updates a orderItem.
     *
     * @param orderItemDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OrderItemDTO> update(OrderItemDTO orderItemDTO);

    /**
     * Partially updates a orderItem.
     *
     * @param orderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OrderItemDTO> partialUpdate(OrderItemDTO orderItemDTO);

    /**
     * Get all the orderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<OrderItemDTO> findAll(Pageable pageable);

    /**
     * Get all the orderItems with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<OrderItemDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of orderItems available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" orderItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OrderItemDTO> findOne(Long id);

    /**
     * Delete the "id" orderItem.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
