package com.lims.api.audit.sql;

import java.util.List;

public interface AuditSqlGenerator {

    String makeSelectSqlWithComment(List<String> columnNames, String tableName);

}