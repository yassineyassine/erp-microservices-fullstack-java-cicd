package com.soft.erp.store.service;

import com.soft.erp.store.service.dto.CustomerDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.soft.erp.store.domain.Customer}.
 */
public interface CustomerService {
    /**
     * Save a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CustomerDTO> save(CustomerDTO customerDTO);

    /**
     * Updates a customer.
     *
     * @param customerDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CustomerDTO> update(CustomerDTO customerDTO);

    /**
     * Partially updates a customer.
     *
     * @param customerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CustomerDTO> partialUpdate(CustomerDTO customerDTO);

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CustomerDTO> findAll(Pageable pageable);

    /**
     * Returns the number of customers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" customer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CustomerDTO> findOne(Long id);

    /**
     * Delete the "id" customer.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
