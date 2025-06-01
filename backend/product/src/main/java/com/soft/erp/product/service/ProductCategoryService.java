package com.soft.erp.product.service;

import com.soft.erp.product.service.dto.ProductCategoryDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.soft.erp.product.domain.ProductCategory}.
 */
public interface ProductCategoryService {
    /**
     * Save a productCategory.
     *
     * @param productCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProductCategoryDTO> save(ProductCategoryDTO productCategoryDTO);

    /**
     * Updates a productCategory.
     *
     * @param productCategoryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProductCategoryDTO> update(ProductCategoryDTO productCategoryDTO);

    /**
     * Partially updates a productCategory.
     *
     * @param productCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProductCategoryDTO> partialUpdate(ProductCategoryDTO productCategoryDTO);

    /**
     * Get all the productCategories.
     *
     * @return the list of entities.
     */
    Flux<ProductCategoryDTO> findAll();

    /**
     * Returns the number of productCategories available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" productCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProductCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" productCategory.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
