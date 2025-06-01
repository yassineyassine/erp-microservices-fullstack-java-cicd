package com.soft.erp.invoice.service.mapper;

import com.soft.erp.invoice.domain.Invoice;
import com.soft.erp.invoice.service.dto.InvoiceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {}
