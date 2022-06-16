package com.lims.api.common.service.impl;

import com.lims.api.common.annotation.Audit;
import com.lims.api.common.service.AuditTrailService;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class RegularAuditService implements AuditTrailService {

    @Override
    public void recordAuditTrail(Invocation invocation) throws SQLException {
        Object[] args = invocation.getArgs();
        Statement statement = (Statement) args[0];
        StatementHandler handler = (StatementHandler) invocation.getTarget();


        String sql = handler.getBoundSql().getSql();
        Object parameterObject = handler.getParameterHandler().getParameterObject();
        String parameter = parameterObject == null ? null : parameterObject.toString();
        ResultSet resultSet = statement.getResultSet();
    }

    @Override
    public boolean hasAnnotation(Method method) {
        return method != null && method.getAnnotation(Audit.class) != null;
    }
}