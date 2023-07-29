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
