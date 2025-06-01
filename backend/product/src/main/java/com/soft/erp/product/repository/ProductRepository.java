package com.soft.erp.product.repository;

import com.soft.erp.product.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long>, ProductRepositoryInternal {
    Flux<Product> findAllBy(Pageable pageable);

    @Override
    Mono<Product> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Product> findAllWithEagerRelationships();

    @Override
    Flux<Product> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM product entity WHERE entity.product_category_id = :id")
    Flux<Product> findByProductCategory(Long id);

    @Query("SELECT * FROM product entity WHERE entity.product_category_id IS NULL")
    Flux<Product> findAllWhereProductCategoryIsNull();

    @Override
    <S extends Product> Mono<S> save(S entity);

    @Override
    Flux<Product> findAll();

    @Override
    Mono<Product> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductRepositoryInternal {
    <S extends Product> Mono<S> save(S entity);

    Flux<Product> findAllBy(Pageable pageable);

    Flux<Product> findAll();

    Mono<Product> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Product> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Product> findOneWithEagerRelationships(Long id);

    Flux<Product> findAllWithEagerRelationships();

    Flux<Product> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
