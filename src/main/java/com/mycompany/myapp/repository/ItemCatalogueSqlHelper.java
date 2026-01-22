package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ItemCatalogueSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("catalogue_code", table, columnPrefix + "_catalogue_code"));
        columns.add(Column.aliased("active", table, columnPrefix + "_active"));

        columns.add(Column.aliased("catalogue_id", table, columnPrefix + "_catalogue_id"));
        return columns;
    }
}
