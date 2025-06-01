package com.soft.erp.invoice.repository.rowmapper;

import com.soft.erp.invoice.domain.Shipment;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Shipment}, with proper type conversions.
 */
@Service
public class ShipmentRowMapper implements BiFunction<Row, String, Shipment> {

    private final ColumnConverter converter;

    public ShipmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Shipment} stored in the database.
     */
    @Override
    public Shipment apply(Row row, String prefix) {
        Shipment entity = new Shipment();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTrackingCode(converter.fromRow(row, prefix + "_tracking_code", String.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setDetails(converter.fromRow(row, prefix + "_details", String.class));
        entity.setInvoiceId(converter.fromRow(row, prefix + "_invoice_id", Long.class));
        return entity;
    }
}
