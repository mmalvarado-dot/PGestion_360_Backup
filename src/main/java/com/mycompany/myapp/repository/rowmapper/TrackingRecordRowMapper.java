package com.mycompany.myapp.repository.rowmapper;

// 1. IMPORTS CORRECTOS
import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.domain.enumeration.TrackingActionType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

// NOTA: No necesitamos importar ColumnConverter porque está en este mismo paquete.

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

        // --- ID PRINCIPAL ---
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));

        // --- FECHA (Instant) ---
        entity.setChangeDate(converter.fromRow(row, prefix + "_change_date", Instant.class));

        // --- CAMPOS DE TEXTO ---
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setComments(converter.fromRow(row, prefix + "_comments", String.class));

        // --- ENUM (Tipo de Acción) ---
        entity.setActionType(converter.fromRow(row, prefix + "_action_type", TrackingActionType.class));

        // --- RELACIONES (IDs) ---
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setResponsibleId(converter.fromRow(row, prefix + "_responsible_id", Long.class));
        entity.setChangeRequestId(converter.fromRow(row, prefix + "_change_request_id", Long.class));

        // --- EL CAMPO QUE ARREGLA EL FORMULARIO DE EDICIÓN ---
        // Leemos el ID del departamento desde la base de datos
        entity.setDepartmentId(converter.fromRow(row, prefix + "_department_id", Long.class));

        return entity;
    }
}
