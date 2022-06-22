package com.lims.api.audit.service;

import java.util.List;

public interface SqlGenerator {

    public String makeSelectSqlWithComment(List<String> columnNames, String tableName, String conditions);

}