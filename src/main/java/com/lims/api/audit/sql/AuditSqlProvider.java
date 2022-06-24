package com.lims.api.audit.sql;

import com.lims.api.audit.annotation.AuditId;
import com.lims.api.audit.domain.SqlEntity;
import com.lims.api.audit.domain.SqlParameter;
import com.lims.api.audit.util.StringConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuditSqlProvider {

    private final String conditionOperator = " WHERE ";
    private final AuditSqlGenerator generator;
    private final StringConverter converter;

    public AuditSqlProvider(AuditSqlGeneratorFactory factory, StringConverter converter) {
        this.generator = factory.create();
        this.converter = converter;
    }

    public String generateSelectClause(Class<?> clazz, String tableName) {
        List<String> columnNames = makeColumnNames(clazz);
        return generator.makeSelectSqlWithComment(columnNames, tableName);
    }

    public String makeConditionClause(List<SqlParameter> sqlParameters) {
        return conditionOperator + sqlParameters.stream()
                .map(p -> p.getName() + " = ?")
                .collect(Collectors.joining(" AND "));
    }

    public List<SqlParameter> getSqlParameter(SqlEntity entity, Object[] parameters) {
        assertExistsParameter(parameters);

        Object parameter = parameters[0];
        Class<?> parameterClazz = parameter.getClass();
        List<String> parameterFields = List.of(parameterClazz.getDeclaredFields()).stream()
                .map(f -> f.getName())
                .collect(Collectors.toList());

        List<String> idFields = getParameterFields(entity.getIdFields(), parameterFields);
        List<SqlParameter> sqlParameters = new ArrayList<>();

        try {
            for (String fieldName : idFields) {
                Field parameterField = parameterClazz.getDeclaredField(fieldName);
                parameterField.setAccessible(true);

                SqlParameter sqlParameter = new SqlParameter();
                sqlParameter.setName(converter.convert(parameterField.getName()));
                sqlParameter.setData(parameterField.get(parameter));

                sqlParameters.add(sqlParameter);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Parameter '" + parameterClazz.getName() + "' has not a field matching the id field in the audit entity. '" + entity.getName() + "' [" + e.getMessage() + "]", e.getCause());
        }
        return sqlParameters;
    }

    private List<String> getParameterFields(List<String> idFields, List<String> parameterFields) {
        List<String> filteredFields = idFields.stream().filter(s -> parameterFields.contains(s)).collect(Collectors.toList());
        boolean isSameFields = idFields.size() == filteredFields.size();
        return isSameFields ? idFields : filteredFields;
    }

    public List<String> getIdFields(Class<?> clazz) {
        List<String> ids = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(AuditId.class))
                .map(field -> field.getName())
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            throw new RuntimeException("There is no field with 'AuditId' annotation in the '" + clazz.getSimpleName() + "'. [" + clazz.getName() + "]");
        }
        return ids;
    }

    private List<String> makeColumnNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> converter.convert(field.getName()))
                .collect(Collectors.toList());
    }

    private void assertExistsParameter(Object[] parameters) {
        if (parameters.length > 0) {
            return;
        }
        throw new IllegalArgumentException("Not found sql parameters. Parameter is required. [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
    }
}