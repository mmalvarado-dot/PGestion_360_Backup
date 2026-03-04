package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ChangeRequestSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("created_date", table, columnPrefix + "_created_date"));
        columns.add(Column.aliased("updated_date", table, columnPrefix + "_updated_date"));
        columns.add(Column.aliased("priority", table, columnPrefix + "_priority"));
        columns.add(Column.aliased("impact", table, columnPrefix + "_impact"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("fecha_entrega", table, columnPrefix + "_fecha_entrega"));
        columns.add(Column.aliased("observaciones", table, columnPrefix + "_observaciones"));
        columns.add(Column.aliased("archivo_adjunto", table, columnPrefix + "_archivo_adjunto"));
        columns.add(Column.aliased("archivo_adjunto_content_type", table, columnPrefix + "_archivo_adjunto_content_type"));
        columns.add(Column.aliased("solicitante", table, columnPrefix + "_solicitante"));
        columns.add(Column.aliased("departamento", table, columnPrefix + "_departamento"));

        // --- CAMBIADO DE RESPONSIBLE_ID A USER_ID ---
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        // --------------------------------------------

        columns.add(Column.aliased("item_catalogue_id", table, columnPrefix + "_item_catalogue_id"));
        return columns;
    }
}
