package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.SqlEntity;
import com.lims.api.audit.domain.SqlParameter;
import com.lims.api.audit.service.AuditSqlGenerator;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AuditSqlProvider {

    private final AuditSqlGenerator generator;
    private final SqlEntity entity;

    public AuditSqlProvider(AuditSqlGenerator generator, SqlEntity entity) {
        this.generator = generator;
        this.entity = entity;
    }

    public String generateSelectSql(List<SqlParameter> sqlParameters) {
        List<String> columnNames = makeColumnNames(entity.getTarget());
        String tableName = entity.getName();
        String conditions = makeConditions(sqlParameters);
        return generator.makeSelectSqlWithComment(columnNames, tableName, conditions);
    }

    public List<SqlParameter> getSqlParameter(Object[] parameters) {
        assertExistsParameter(parameters);

        Object parameter = parameters[0];
        Class<?> parameterClazz = parameter.getClass();
        List<Field> idFields = entity.getIdFields();
        List<SqlParameter> sqlParameters = new ArrayList<>();

        try {
            for (Field field : idFields) {
                Field parameterField = parameterClazz.getDeclaredField(field.getName());
                parameterField.setAccessible(true);

                SqlParameter sqlParameter = new SqlParameter();
                sqlParameter.setName(convertCamelToSnakeCase(parameterField.getName()));
                sqlParameter.setValue(parameterField.get(parameter));

                sqlParameters.add(sqlParameter);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Parameter '" + parameterClazz.getName() + "' has not a field matching the id field in the audit entity '" + entity.getName() + "'. [" + e.getMessage() + "]");
        }
        return sqlParameters;
    }

    private List<String> makeColumnNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> convertCamelToSnakeCase(field.getName()))
                .collect(Collectors.toList());
    }

    private String makeConditions(List<SqlParameter> sqlParameters) {
        return sqlParameters.stream().map(k -> k.getName() + " = ?").collect(Collectors.joining(" AND "));
    }

    private void assertExistsParameter(Object[] parameters) {
        if (parameters.length > 0) {
            return;
        }
        throw new IllegalArgumentException("Not found sql parameters. Parameter is required. [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
    }

    // TODO converter로 의존성 분리 및 다형성
    private String convertCamelToSnakeCase(String str) {
        String ret = str.replaceAll("([A-Z])", "_$1").replaceAll("([a-z][A-Z])", "$1_$2");
        return ret.toLowerCase();
    }
}
