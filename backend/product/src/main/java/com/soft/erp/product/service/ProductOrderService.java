package com.soft.erp.product.service;

import com.soft.erp.product.service.dto.ProductOrderDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.soft.erp.product.domain.ProductOrder}.
 */
public interface ProductOrderService {
    /**
     * Save a productOrder.
     *
     * @param productOrderDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProductOrderDTO> save(ProductOrderDTO productOrderDTO);

    /**
     * Updates a productOrder.
     *
     * @param productOrderDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProductOrderDTO> update(ProductOrderDTO productOrderDTO);

    /**
     * Partially updates a productOrder.
     *
     * @param productOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProductOrderDTO> partialUpdate(ProductOrderDTO productOrderDTO);

    /**
     * Get all the productOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProductOrderDTO> findAll(Pageable pageable);

    /**
     * Returns the number of productOrders available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" productOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProductOrderDTO> findOne(Long id);

    /**
     * Delete the "id" productOrder.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
