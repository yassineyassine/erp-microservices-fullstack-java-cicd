package com.soft.erp.invoice.service;

import com.soft.erp.invoice.service.dto.InvoiceDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.soft.erp.invoice.domain.Invoice}.
 */
public interface InvoiceService {
    /**
     * Save a invoice.
     *
     * @param invoiceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InvoiceDTO> save(InvoiceDTO invoiceDTO);

    /**
     * Updates a invoice.
     *
     * @param invoiceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InvoiceDTO> update(InvoiceDTO invoiceDTO);

    /**
     * Partially updates a invoice.
     *
     * @param invoiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InvoiceDTO> partialUpdate(InvoiceDTO invoiceDTO);

    /**
     * Get all the invoices.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InvoiceDTO> findAll(Pageable pageable);

    /**
     * Returns the number of invoices available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" invoice.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InvoiceDTO> findOne(Long id);

    /**
     * Delete the "id" invoice.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
