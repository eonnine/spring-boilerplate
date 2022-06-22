package com.lims.api.audit.service.impl;

import java.util.List;
import java.util.stream.Collectors;

public class OracleAuditSqlGenerator extends AbstractSqlGenerator {

    private final String commentQuery = "SELECT LISTAGG(DISTINCT COMMENTS, ',') FROM ALL_COL_COMMENTS WHERE UPPER(TABLE_NAME) = UPPER('%s') AND UPPER(COLUMN_NAME) = UPPER('%s')";

    @Override
    public String makeSelectSqlWithComment(List<String> columnNames, String tableName, String conditions) {
        String columnsWithComments = makeColumnsWithComments(tableName, columnNames);
        return String.format("SELECT %s FROM %s WHERE %s", columnsWithComments, tableName, conditions);
    }

    private String makeColumnsWithComments(String tableName, List<String> columnNames) {
        return columnNames.stream()
                .map(s -> s + ", " + "( " + String.format(commentQuery, tableName, s) + " ) AS " + addCommentSuffix(s))
                .collect(Collectors.joining(", "));
    }
}