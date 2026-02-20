package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Department;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Department}, with proper type conversions.
 */
@Service
public class DepartmentRowMapper implements BiFunction<Row, String, Department> {

    private final ColumnConverter converter;

    public DepartmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Department} stored in the database.
     */
    @Override
    public Department apply(Row row, String prefix) {
        // --- VALIDACIÓN CRÍTICA (AGREGAR ESTO) ---
        // Si el ID del departamento es NULL, significa que el LEFT JOIN no encontró nada.
        // Debemos devolver NULL para que la entidad TrackingRecord sepa que no hay departamento.
        Object id = converter.fromRow(row, prefix + "_id", Long.class);
        if (id == null) {
            return null;
        }
        // ----------------------------------------

        Department entity = new Department();
        entity.setId((Long) id);
        entity.setDepartmentName(converter.fromRow(row, prefix + "_department_name", String.class));
        entity.setField(converter.fromRow(row, prefix + "_field", String.class));
        entity.setParentDepartmentId(converter.fromRow(row, prefix + "_parent_department_id", Long.class));
        return entity;
    }
}
