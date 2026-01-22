package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Catalogue;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Catalogue}, with proper type conversions.
 */
@Service
public class CatalogueRowMapper implements BiFunction<Row, String, Catalogue> {

    private final ColumnConverter converter;

    public CatalogueRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Catalogue} stored in the database.
     */
    @Override
    public Catalogue apply(Row row, String prefix) {
        Catalogue entity = new Catalogue();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", Boolean.class));
        return entity;
    }
}
