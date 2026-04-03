package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Responsible;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Responsible}, with proper type conversions.
 */
@Service
public class ResponsibleRowMapper implements BiFunction<Row, String, Responsible> {

    private final ColumnConverter converter;

    public ResponsibleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Responsible} stored in the database.
     */
    @Override
    public Responsible apply(Row row, String prefix) {
        // --- MODIFICACIÓN CRÍTICA ---
        // 1. Extraemos el ID primero.
        Object id = converter.fromRow(row, prefix + "_id", Long.class);

        if (id == null) {
            return null;
        }
        // ---------------------------

        Responsible entity = new Responsible();
        entity.setId((Long) id);
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setPosition(converter.fromRow(row, prefix + "_position", String.class));
        return entity;
    }
}
