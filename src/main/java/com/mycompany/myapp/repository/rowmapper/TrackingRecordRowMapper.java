package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.domain.enumeration.TrackingActionType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrackingRecord}, with proper type conversions.
 */
@Service
public class TrackingRecordRowMapper implements BiFunction<Row, String, TrackingRecord> {

    private final ColumnConverter converter;

    public TrackingRecordRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrackingRecord} stored in the database.
     */
    @Override
    public TrackingRecord apply(Row row, String prefix) {
        TrackingRecord entity = new TrackingRecord();

        // Escudo protector para alinear las columnas (evita e__id)
        String p = (prefix != null && prefix.endsWith("_")) ? prefix.substring(0, prefix.length() - 1) : prefix;
        String cleanPrefix = (p == null || p.isEmpty()) ? "" : p + "_";

        // --- ID PRINCIPAL ---
        entity.setId(converter.fromRow(row, cleanPrefix + "id", Long.class));

        // --- FECHA (Instant) ---
        entity.setChangeDate(converter.fromRow(row, cleanPrefix + "change_date", Instant.class));

        // --- CAMPOS DE TEXTO ---
        entity.setStatus(converter.fromRow(row, cleanPrefix + "status", String.class));
        entity.setComments(converter.fromRow(row, cleanPrefix + "comments", String.class));

        // --- ENUM (Tipo de Acción) ---
        entity.setActionType(converter.fromRow(row, cleanPrefix + "action_type", TrackingActionType.class));

        // --- RELACIONES (IDs) ---
        entity.setUserId(converter.fromRow(row, cleanPrefix + "user_id", Long.class));
        //entity.setResponsibleId(converter.fromRow(row, cleanPrefix + "responsible_id", Long.class));
        entity.setChangeRequestId(converter.fromRow(row, cleanPrefix + "change_request_id", Long.class));
        entity.setDepartmentId(converter.fromRow(row, cleanPrefix + "department_id", Long.class));

        return entity;
    }
}
