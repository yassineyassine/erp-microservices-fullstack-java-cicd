package com.soft.erp.product.service.mapper;

import com.soft.erp.product.domain.OrderItem;
import com.soft.erp.product.domain.Product;
import com.soft.erp.product.domain.ProductOrder;
import com.soft.erp.product.service.dto.OrderItemDTO;
import com.soft.erp.product.service.dto.ProductDTO;
import com.soft.erp.product.service.dto.ProductOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    @Mapping(target = "order", source = "order", qualifiedByName = "productOrderCode")
    OrderItemDTO toDto(OrderItem s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);

    @Named("productOrderCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    ProductOrderDTO toDtoProductOrderCode(ProductOrder productOrder);
}
