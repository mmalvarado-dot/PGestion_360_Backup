package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TrackingRecordSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("change_date", table, columnPrefix + "_change_date"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("comments", table, columnPrefix + "_comments"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("responsible_id", table, columnPrefix + "_responsible_id"));
        columns.add(Column.aliased("change_request_id", table, columnPrefix + "_change_request_id"));
        return columns;
    }
}
