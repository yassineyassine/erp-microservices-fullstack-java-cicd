package com.soft.erp.product.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.soft.erp.product.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductOrderDTO.class);
        ProductOrderDTO productOrderDTO1 = new ProductOrderDTO();
        productOrderDTO1.setId(1L);
        ProductOrderDTO productOrderDTO2 = new ProductOrderDTO();
        assertThat(productOrderDTO1).isNotEqualTo(productOrderDTO2);
        productOrderDTO2.setId(productOrderDTO1.getId());
        assertThat(productOrderDTO1).isEqualTo(productOrderDTO2);
        productOrderDTO2.setId(2L);
        assertThat(productOrderDTO1).isNotEqualTo(productOrderDTO2);
        productOrderDTO1.setId(null);
        assertThat(productOrderDTO1).isNotEqualTo(productOrderDTO2);
    }
}
