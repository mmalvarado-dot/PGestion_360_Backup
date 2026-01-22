package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.ItemCatalogue;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ItemCatalogue}, with proper type conversions.
 */
@Service
public class ItemCatalogueRowMapper implements BiFunction<Row, String, ItemCatalogue> {

    private final ColumnConverter converter;

    public ItemCatalogueRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ItemCatalogue} stored in the database.
     */
    @Override
    public ItemCatalogue apply(Row row, String prefix) {
        ItemCatalogue entity = new ItemCatalogue();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setCatalogueCode(converter.fromRow(row, prefix + "_catalogue_code", String.class));
        entity.setActive(converter.fromRow(row, prefix + "_active", Boolean.class));
        entity.setCatalogueId(converter.fromRow(row, prefix + "_catalogue_id", Long.class));
        return entity;
    }
}
