package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FileRecordSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("file_name", table, columnPrefix + "_file_name"));
        columns.add(Column.aliased("file_path", table, columnPrefix + "_file_path"));
        columns.add(Column.aliased("file_type", table, columnPrefix + "_file_type"));
        columns.add(Column.aliased("content", table, columnPrefix + "_content"));
        columns.add(Column.aliased("content_content_type", table, columnPrefix + "_content_content_type"));

        columns.add(Column.aliased("upload_date", table, columnPrefix + "_upload_date"));

        columns.add(Column.aliased("change_request_id", table, columnPrefix + "_change_request_id"));
        return columns;
    }
}
