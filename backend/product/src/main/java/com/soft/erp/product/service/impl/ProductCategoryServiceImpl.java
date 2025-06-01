package com.soft.erp.product.service.impl;

import com.soft.erp.product.repository.ProductCategoryRepository;
import com.soft.erp.product.service.ProductCategoryService;
import com.soft.erp.product.service.dto.ProductCategoryDTO;
import com.soft.erp.product.service.mapper.ProductCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.soft.erp.product.domain.ProductCategory}.
 */
@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository, ProductCategoryMapper productCategoryMapper) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
    }

    @Override
    public Mono<ProductCategoryDTO> save(ProductCategoryDTO productCategoryDTO) {
        LOG.debug("Request to save ProductCategory : {}", productCategoryDTO);
        return productCategoryRepository.save(productCategoryMapper.toEntity(productCategoryDTO)).map(productCategoryMapper::toDto);
    }

    @Override
    public Mono<ProductCategoryDTO> update(ProductCategoryDTO productCategoryDTO) {
        LOG.debug("Request to update ProductCategory : {}", productCategoryDTO);
        return productCategoryRepository.save(productCategoryMapper.toEntity(productCategoryDTO)).map(productCategoryMapper::toDto);
    }

    @Override
    public Mono<ProductCategoryDTO> partialUpdate(ProductCategoryDTO productCategoryDTO) {
        LOG.debug("Request to partially update ProductCategory : {}", productCategoryDTO);

        return productCategoryRepository
            .findById(productCategoryDTO.getId())
            .map(existingProductCategory -> {
                productCategoryMapper.partialUpdate(existingProductCategory, productCategoryDTO);

                return existingProductCategory;
            })
            .flatMap(productCategoryRepository::save)
            .map(productCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProductCategoryDTO> findAll() {
        LOG.debug("Request to get all ProductCategories");
        return productCategoryRepository.findAll().map(productCategoryMapper::toDto);
    }

    public Mono<Long> countAll() {
        return productCategoryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProductCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get ProductCategory : {}", id);
        return productCategoryRepository.findById(id).map(productCategoryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ProductCategory : {}", id);
        return productCategoryRepository.deleteById(id);
    }
}
