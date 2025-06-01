package com.soft.erp.invoice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.soft.erp.invoice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShipmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentDTO.class);
        ShipmentDTO shipmentDTO1 = new ShipmentDTO();
        shipmentDTO1.setId(1L);
        ShipmentDTO shipmentDTO2 = new ShipmentDTO();
        assertThat(shipmentDTO1).isNotEqualTo(shipmentDTO2);
        shipmentDTO2.setId(shipmentDTO1.getId());
        assertThat(shipmentDTO1).isEqualTo(shipmentDTO2);
        shipmentDTO2.setId(2L);
        assertThat(shipmentDTO1).isNotEqualTo(shipmentDTO2);
        shipmentDTO1.setId(null);
        assertThat(shipmentDTO1).isNotEqualTo(shipmentDTO2);
    }
}
