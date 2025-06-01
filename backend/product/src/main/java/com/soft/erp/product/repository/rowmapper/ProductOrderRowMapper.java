package com.soft.erp.product.repository.rowmapper;

import com.soft.erp.product.domain.ProductOrder;
import com.soft.erp.product.domain.enumeration.OrderStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductOrder}, with proper type conversions.
 */
@Service
public class ProductOrderRowMapper implements BiFunction<Row, String, ProductOrder> {

    private final ColumnConverter converter;

    public ProductOrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductOrder} stored in the database.
     */
    @Override
    public ProductOrder apply(Row row, String prefix) {
        ProductOrder entity = new ProductOrder();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPlacedDate(converter.fromRow(row, prefix + "_placed_date", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", OrderStatus.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setInvoiceId(converter.fromRow(row, prefix + "_invoice_id", Long.class));
        entity.setCustomer(converter.fromRow(row, prefix + "_customer", String.class));
        return entity;
    }
}
