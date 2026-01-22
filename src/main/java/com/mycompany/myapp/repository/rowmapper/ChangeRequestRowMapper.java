package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.enumeration.Impacto;
import com.mycompany.myapp.domain.enumeration.prioridad;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ChangeRequest}, with proper type conversions.
 */
@Service
public class ChangeRequestRowMapper implements BiFunction<Row, String, ChangeRequest> {

    private final ColumnConverter converter;

    public ChangeRequestRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ChangeRequest} stored in the database.
     */
    @Override
    public ChangeRequest apply(Row row, String prefix) {
        ChangeRequest entity = new ChangeRequest();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", LocalDate.class));
        entity.setUpdatedDate(converter.fromRow(row, prefix + "_updated_date", LocalDate.class));
        entity.setPriority(converter.fromRow(row, prefix + "_priority", prioridad.class));
        entity.setImpact(converter.fromRow(row, prefix + "_impact", Impacto.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setFechaEntrega(converter.fromRow(row, prefix + "_fecha_entrega", LocalDate.class));
        entity.setObservaciones(converter.fromRow(row, prefix + "_observaciones", String.class));
        entity.setArchivoAdjuntoContentType(converter.fromRow(row, prefix + "_archivo_adjunto_content_type", String.class));
        entity.setArchivoAdjunto(converter.fromRow(row, prefix + "_archivo_adjunto", byte[].class));
        entity.setSolicitante(converter.fromRow(row, prefix + "_solicitante", String.class));
        entity.setDepartamento(converter.fromRow(row, prefix + "_departamento", String.class));
        entity.setResponsibleId(converter.fromRow(row, prefix + "_responsible_id", Long.class));
        entity.setItemCatalogueId(converter.fromRow(row, prefix + "_item_catalogue_id", Long.class));
        return entity;
    }
}
