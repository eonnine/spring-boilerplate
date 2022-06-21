package com.lims.api.audit.service;

import java.util.List;

public interface AuditSqlGenerator {

    public String makeSelectSqlWithComment(List<String> columnNames, String tableName, String conditions);

}
