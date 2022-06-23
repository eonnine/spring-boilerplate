package com.lims.api.audit.sql;

import java.util.List;

public interface AuditSqlGenerator {

    public String makeSelectSqlWithComment(List<String> columnNames, String tableName, String conditions);

}