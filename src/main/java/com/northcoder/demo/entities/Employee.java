package com.northcoder.demo.entities;

import java.time.LocalDate;
import java.util.Map;

public record Employee(
        String name,
        String position,
        String office,
        Integer age,
        LocalDate startDate,
        String salary) {

    // as well as mapping from entity field names to DB column names, this
    // acts as a "safe list" to ensure the DataTable column names are handled
    //  safely when building a dynamically generated SQL statement.
    private static final Map<String, String> fieldToColumn = Map.of(
            "name", "name",
            "position", "position",
            "office", "office",
            "age", "age",
            "startDate", "start_date",
            "salary", "salary"
    );

    public static String sqlColumn(String field) {
        return fieldToColumn.get(field);
    }
}
