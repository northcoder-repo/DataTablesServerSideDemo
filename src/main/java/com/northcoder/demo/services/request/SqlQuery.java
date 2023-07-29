package com.northcoder.demo.services.request;

import com.northcoder.demo.entities.Employee;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

//
// converts the DataTables request (ServerSideRequest) into a SQL prepared
// statement string, together with parameters and parameter data types:
//
public class SqlQuery {

    private static final String DB = "datatables";
    private static final String TABLE = "employees";

    private final String countString;
    private final String sqlString;
    private final Object[] params;
    private final int[] paramTypes;

    private final List<Object> paramsList = new ArrayList<>();
    private final List<Integer> paramTypesList = new ArrayList<>();

    public SqlQuery(ServerSideRequest ssr) {
        String countClause = "select count(1) ";
        String selectClause = buildSelectClause(ssr);
        String fromClause = buildFromClause();
        String whereClauseGlob = buildGlobalWhereClause(ssr);
        String whereClauseCols = buildColumnsWhereClause(ssr, whereClauseGlob);
        String orderByClause = buildOrderByClause(ssr);
        String limitOffsetClause = buildLimitOffsetClause(ssr);

        // how many rows would be selected if we did not use LIMIT and OFFSET:
        this.countString = countClause + fromClause + whereClauseGlob + whereClauseCols;
        // only one page of DataTables results (using LIMIT and OFFSET):
        this.sqlString = selectClause + fromClause + whereClauseGlob + whereClauseCols
                + orderByClause + limitOffsetClause;

        this.params = paramsList.toArray(Object[]::new);
        this.paramTypes = paramTypesList.stream().mapToInt(i -> i).toArray();
    }

    public String getCountString() {
        return countString;
    }

    public String getSqlString() {
        return sqlString;
    }

    public Object[] getParams() {
        return params;
    }

    public int[] getParamTypes() {
        return paramTypes;
    }

    private String buildSelectClause(ServerSideRequest ssr) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (Column column : ssr.columns()) {
            sb.append(Employee.sqlColumn(column.data())).append(", ");
        }
        sb.setLength(sb.length() - 2); // remove the final ", "
        sb.append(" ");
        return sb.toString();
    }

    private String buildFromClause() {
        StringBuilder sb = new StringBuilder();
        sb.append("from ").append(DB).append(".").append(TABLE).append(" ");
        return sb.toString();
    }

    private String buildGlobalWhereClause(ServerSideRequest ssr) {
        //
        // This logic assumes the database is MySQL and the table's collation is
        // utf8mb4_0900_ai_ci, where:
        //  - ai = accent-insensitive
        //  - ci = case-insensitive
        // Therefore no explicit handling of case or accents is needed here.
        // See https://dev.mysql.com/doc/refman/8.0/en/charset-collation-names.html
        //
        StringBuilder sb = new StringBuilder();
        // Neutralize SQL `like` wildcards - try to start search strings
        // with literal text to decrease likelihood of table scans:
        String globalSearch = ssr.search().value()
                .replace("%", "\\%")
                .replace("_", "\\_");
        if (globalSearch.isEmpty()) {
            return "";
        }

        // make sure at least one field is searchable:
        long count = ssr.columns().stream().filter(col -> col.searchable()).count();
        if (count == 0) {
            return "";
        }

        sb.append("where (");
        for (Column column : ssr.columns()) {
            if (column.searchable()) {
                sb.append(Employee.sqlColumn(column.data()))
                        // SQL: "like 'foo%'..."
                        // effectively, a "starts with" query:
                        .append(" like concat(?, '%') or ");
                // prepared statement parameters and types:
                paramsList.add(globalSearch);
                paramTypesList.add(Types.VARCHAR);
            }
        }
        sb.setLength(sb.length() - 4); // remove the final " or "
        return sb.append(") ").toString();
    }

    private String buildColumnsWhereClause(ServerSideRequest ssr, String whereClauseGlob) {
        // just a demo using one column - the Office col, as a
        // drop-down list, consisting of an exact office name

        // first see if we have any column filter values:
        long count = ssr.columns().stream().filter(
                col -> col.search().value() != null && !col.search().value().isEmpty()
        ).count();
        if (count == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (whereClauseGlob.isBlank()) {
            // start of the WHERE clause:
            sb.append("where ");
        } else {
            // WHERE clause already started:
            sb.append("and ");
        }

        for (Column col : ssr.columns()) {
            if (col.searchable() && col.search().value() != null && !col.search().value().isBlank()) {
                sb.append(Employee.sqlColumn(col.data()))
                        // just our one demo drop-down column:
                        .append(" = ? and ");
                // prepared statement parameters and types:
                paramsList.add(col.search().value());
                paramTypesList.add(Types.VARCHAR);
            }
        }
        sb.setLength(sb.length() - 4); // remove the final "and "
        return sb.toString();
    }

    private String buildOrderByClause(ServerSideRequest ssr) {
        StringBuilder sb = new StringBuilder();
        if (ssr.order().isEmpty()) {
            return "";
        }
        sb.append("order by ");
        for (OrderCol orderCol : ssr.order()) {
            sb.append(orderCol.column() + 1).append(" ").append(orderCol.dir()).append(", ");
        }
        sb.setLength(sb.length() - 2); // remove the final ", "
        sb.append(" ");
        return sb.toString();
    }

    private String buildLimitOffsetClause(ServerSideRequest ssr) {
        // get the currently requested page of data (for DataTables):
        return "limit " + ssr.length() + " offset " + ssr.start();
    }

}
