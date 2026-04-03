package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TrackingRecordSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();

        String p = (columnPrefix != null && columnPrefix.endsWith("_"))
            ? columnPrefix.substring(0, columnPrefix.length() - 1)
            : columnPrefix;
        String prefix = (p == null || p.isEmpty()) ? "" : p + "_";

        columns.add(Column.aliased("id", table, prefix + "id"));
        columns.add(Column.aliased("change_date", table, prefix + "change_date"));
        columns.add(Column.aliased("status", table, prefix + "status"));
        columns.add(Column.aliased("comments", table, prefix + "comments"));
        columns.add(Column.aliased("action_type", table, prefix + "action_type")); // ¡Agregado!

        columns.add(Column.aliased("user_id", table, prefix + "user_id"));
        columns.add(Column.aliased("responsible_id", table, prefix + "responsible_id"));
        columns.add(Column.aliased("change_request_id", table, prefix + "change_request_id"));
        columns.add(Column.aliased("department_id", table, prefix + "department_id")); // ¡Agregado!

        return columns;
    }
}
