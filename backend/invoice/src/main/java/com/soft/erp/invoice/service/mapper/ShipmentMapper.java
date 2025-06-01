package com.soft.erp.invoice.service.mapper;

import com.soft.erp.invoice.domain.Invoice;
import com.soft.erp.invoice.domain.Shipment;
import com.soft.erp.invoice.service.dto.InvoiceDTO;
import com.soft.erp.invoice.service.dto.ShipmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shipment} and its DTO {@link ShipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentMapper extends EntityMapper<ShipmentDTO, Shipment> {
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceCode")
    ShipmentDTO toDto(Shipment s);

    @Named("invoiceCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    InvoiceDTO toDtoInvoiceCode(Invoice invoice);
}
