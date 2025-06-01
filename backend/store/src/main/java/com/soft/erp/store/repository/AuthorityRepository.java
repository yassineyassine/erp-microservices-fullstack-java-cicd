package com.soft.erp.store.repository;

import com.soft.erp.store.domain.Authority;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data R2DBC repository for the Authority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorityRepository extends R2dbcRepository<Authority, String> {}
