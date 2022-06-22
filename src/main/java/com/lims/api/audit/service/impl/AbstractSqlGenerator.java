package com.lims.api.audit.service.impl;

import com.lims.api.audit.service.SqlGenerator;

import java.util.List;

public abstract class AbstractSqlGenerator implements SqlGenerator {

    public static final String COMMENT_SUFFIX = "_$$COMMENTS";

    protected final String addCommentSuffix(String str) {
        return str + COMMENT_SUFFIX;
    }

    public abstract String makeSelectSqlWithComment(List<String> columnNames, String tableName, String conditions);

}