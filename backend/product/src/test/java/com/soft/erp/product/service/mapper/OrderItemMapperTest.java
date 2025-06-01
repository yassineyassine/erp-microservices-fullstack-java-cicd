package com.soft.erp.product.service.mapper;

import static com.soft.erp.product.domain.OrderItemAsserts.*;
import static com.soft.erp.product.domain.OrderItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderItemMapperTest {

    private OrderItemMapper orderItemMapper;

    @BeforeEach
    void setUp() {
        orderItemMapper = new OrderItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrderItemSample1();
        var actual = orderItemMapper.toEntity(orderItemMapper.toDto(expected));
        assertOrderItemAllPropertiesEquals(expected, actual);
    }
}
