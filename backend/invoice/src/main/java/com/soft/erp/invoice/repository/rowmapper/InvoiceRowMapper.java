package com.soft.erp.invoice.repository.rowmapper;

import com.soft.erp.invoice.domain.Invoice;
import com.soft.erp.invoice.domain.enumeration.InvoiceStatus;
import com.soft.erp.invoice.domain.enumeration.PaymentMethod;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Invoice}, with proper type conversions.
 */
@Service
public class InvoiceRowMapper implements BiFunction<Row, String, Invoice> {

    private final ColumnConverter converter;

    public InvoiceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Invoice} stored in the database.
     */
    @Override
    public Invoice apply(Row row, String prefix) {
        Invoice entity = new Invoice();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setDetails(converter.fromRow(row, prefix + "_details", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", InvoiceStatus.class));
        entity.setPaymentMethod(converter.fromRow(row, prefix + "_payment_method", PaymentMethod.class));
        entity.setPaymentDate(converter.fromRow(row, prefix + "_payment_date", Instant.class));
        entity.setPaymentAmount(converter.fromRow(row, prefix + "_payment_amount", BigDecimal.class));
        return entity;
    }
}
