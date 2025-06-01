package com.soft.erp.product.service.impl;

import com.soft.erp.product.repository.ProductOrderRepository;
import com.soft.erp.product.service.ProductOrderService;
import com.soft.erp.product.service.dto.ProductOrderDTO;
import com.soft.erp.product.service.mapper.ProductOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.soft.erp.product.domain.ProductOrder}.
 */
@Service
@Transactional
public class ProductOrderServiceImpl implements ProductOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductOrderServiceImpl.class);

    private final ProductOrderRepository productOrderRepository;

    private final ProductOrderMapper productOrderMapper;

    public ProductOrderServiceImpl(ProductOrderRepository productOrderRepository, ProductOrderMapper productOrderMapper) {
        this.productOrderRepository = productOrderRepository;
        this.productOrderMapper = productOrderMapper;
    }

    @Override
    public Mono<ProductOrderDTO> save(ProductOrderDTO productOrderDTO) {
        LOG.debug("Request to save ProductOrder : {}", productOrderDTO);
        return productOrderRepository.save(productOrderMapper.toEntity(productOrderDTO)).map(productOrderMapper::toDto);
    }

    @Override
    public Mono<ProductOrderDTO> update(ProductOrderDTO productOrderDTO) {
        LOG.debug("Request to update ProductOrder : {}", productOrderDTO);
        return productOrderRepository.save(productOrderMapper.toEntity(productOrderDTO)).map(productOrderMapper::toDto);
    }

    @Override
    public Mono<ProductOrderDTO> partialUpdate(ProductOrderDTO productOrderDTO) {
        LOG.debug("Request to partially update ProductOrder : {}", productOrderDTO);

        return productOrderRepository
            .findById(productOrderDTO.getId())
            .map(existingProductOrder -> {
                productOrderMapper.partialUpdate(existingProductOrder, productOrderDTO);

                return existingProductOrder;
            })
            .flatMap(productOrderRepository::save)
            .map(productOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProductOrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProductOrders");
        return productOrderRepository.findAllBy(pageable).map(productOrderMapper::toDto);
    }

    public Mono<Long> countAll() {
        return productOrderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProductOrderDTO> findOne(Long id) {
        LOG.debug("Request to get ProductOrder : {}", id);
        return productOrderRepository.findById(id).map(productOrderMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ProductOrder : {}", id);
        return productOrderRepository.deleteById(id);
    }
}
