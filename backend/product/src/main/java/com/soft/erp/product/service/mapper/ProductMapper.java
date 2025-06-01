package com.soft.erp.product.service.mapper;

import com.soft.erp.product.domain.Product;
import com.soft.erp.product.domain.ProductCategory;
import com.soft.erp.product.service.dto.ProductCategoryDTO;
import com.soft.erp.product.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "productCategory", source = "productCategory", qualifiedByName = "productCategoryName")
    ProductDTO toDto(Product s);

    @Named("productCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductCategoryDTO toDtoProductCategoryName(ProductCategory productCategory);
}
