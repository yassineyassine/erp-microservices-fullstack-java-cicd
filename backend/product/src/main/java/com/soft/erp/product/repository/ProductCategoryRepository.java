package com.soft.erp.product.repository;

import com.soft.erp.product.domain.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductCategoryRepository extends ReactiveCrudRepository<ProductCategory, Long>, ProductCategoryRepositoryInternal {
    @Override
    <S extends ProductCategory> Mono<S> save(S entity);

    @Override
    Flux<ProductCategory> findAll();

    @Override
    Mono<ProductCategory> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductCategoryRepositoryInternal {
    <S extends ProductCategory> Mono<S> save(S entity);

    Flux<ProductCategory> findAllBy(Pageable pageable);

    Flux<ProductCategory> findAll();

    Mono<ProductCategory> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProductCategory> findAllBy(Pageable pageable, Criteria criteria);
}
