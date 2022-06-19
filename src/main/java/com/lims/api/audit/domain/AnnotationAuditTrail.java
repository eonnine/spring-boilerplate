package com.lims.api.audit.domain;

import com.lims.api.audit.annotation.Audit;
import com.lims.api.audit.annotation.AuditEntity;
import com.lims.api.audit.annotation.AuditId;
import com.lims.api.audit.service.InvocationRunner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationAuditTrail {

    private DataSource dataSource;
    private InvocationRunner runner;
    private Method target;
    private Object parameter;

    private List<SqlColumn> origin;

    public AnnotationAuditTrail(DataSource dataSource, Method target, Object parameter, InvocationRunner runner) {
        this.dataSource = dataSource;
        this.runner = runner;
        this.target = target;
        this.parameter = parameter;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void setOriginData(List<SqlColumn> data) {
        origin = data;
    }

    public Object record() throws Throwable {
        if (isIntReturnType(target)) {
            preHandle();
        }

        Object result = runner.run();

        if (isIntReturnType(target) && isUpdated((Integer) result)) {
            postHandle(result);
        }

        return result;
    }

    private void preHandle() throws NoSuchFieldException, SQLException {
        assertHasAuditAnnotation(target);

        Audit auditAnnotation = target.getAnnotation(Audit.class);
        Class<?> entity = auditAnnotation.target();

        assertHasAuditEntityAnnotation(entity);

        AuditEntity entityAnnotation = entity.getAnnotation(AuditEntity.class);
        String tableName = entityAnnotation.name();
        List<String> columnNames = makeColumnNames(entity);
        List<SqlParameter> parameters = getSqlParameter(entity);

        String sql = makeSql(tableName, columnNames, parameters);
        setOriginData(queryAllById(sql, columnNames, parameters));
    }

    private void postHandle(Object result) {
        // TODO 체크 문자열 작성 (old data 기준으로 비교)

        // TODO (grouping 설정에 따라 병합해서 insert or 개별 insert
    }

    private List<SqlColumn> queryAllById(String sql,  List<String> columnNames, List<SqlParameter> parameters) throws SQLException {
        List<SqlColumn> result = new ArrayList<>();

        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);

        for (int i=0; i < parameters.size(); i++) {
            statement.setString(i + 1, String.valueOf(parameters.get(i).getValue()));
        }

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            SqlColumn column = new SqlColumn();
            for (String fieldName : columnNames) {
                column.put(fieldName, resultSet.getString(fieldName));
            }
            result.add(column);
        }
        return result;
    }

    private List<SqlParameter> getSqlParameter(Class<?> entity) throws NoSuchFieldException {
        List<Field> idFields = getIdFields(entity);
        Class<?> parameterClazz = parameter.getClass();
        List<SqlParameter> sqlParameters = new ArrayList<>();
        try {
            for (Field field : idFields) {
                Field parameterField = parameterClazz.getDeclaredField(field.getName());
                parameterField.setAccessible(true);

                SqlParameter sqlParameter = SqlParameter.builder()
                        .name(convertCamelToSnakeCase(parameterField.getName()))
                        .value(parameterField.get(parameter))
                        .build();

                sqlParameters.add(sqlParameter);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new NoSuchFieldException("Parameter '" + parameterClazz.getName() + "' has not a field matching the id field in the audit entity '" + entity.getName() + "'. [" + e.getMessage() + "]");
        }
        return sqlParameters;
    }

    private void postHandle() {

    }

//    public void record(Method method, Object parameter, Object executeResult) throws SQLException, NoSuchFieldException {
//        if (!isTarget(method, executeResult)) {
//            return;
//        }
//
//        assertHasAuditAnnotation(method);
//
//        Audit auditAnnotation = method.getAnnotation(Audit.class);
//        Class<?> entity = auditAnnotation.target();
//
//        assertHasAuditEntityAnnotation(entity);
//
//        AuditEntity entityAnnotation = entity.getAnnotation(AuditEntity.class);
//        String tableName = entityAnnotation.name();
//        List<String> columnNames = makeColumnNames(entity);
//        List<Field> idFields = getIdFields(entity);
//
//        Class<?> parameterClazz = parameter.getClass();
//        List<SqlParameter> sqlParameters = new ArrayList<>();
//        try {
//            for (Field field : idFields) {
//                Field parameterField = parameterClazz.getDeclaredField(field.getName());
//                parameterField.setAccessible(true);
//
//                SqlParameter sqlParameter = SqlParameter.builder()
//                        .name(convertCamelToSnakeCase(parameterField.getName()))
//                        .value(parameterField.get(parameter))
//                        .build();
//
//                sqlParameters.add(sqlParameter);
//            }
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new NoSuchFieldException("Parameter '" + parameterClazz.getName() + "' has not a field matching the id field in the audit entity '" + entity.getName() + "'. [" + e.getMessage() + "]");
//        }
//
//        List<Map<String, String>> targetData = queryTargetData(columnNames, tableName, sqlParameters);
//
//        // TODO AUDIT INSERT
//
//        // TODO
//        if (targetData.size() == 0) {
//            // proceed 전에 insert작업이라 기존 데이터가 없는 것이기 때문에 Audit insert 후 refetch
//        }
//
//        System.out.println(targetData.get(0));
//    }
//
//    private List<Map<String, String>> queryTargetData(List<String> columnNames, String tableName, List<SqlParameter> sqlParameters) throws SQLException {
//        String sql = makeSql(columnNames, tableName, sqlParameters);
//        List<Map<String, String>> result = new ArrayList<>();
//
//        Connection connection = transactionManager.getDataSource().getConnection();
//        PreparedStatement statement = connection.prepareStatement(sql);
//
//        for (int i=0; i < sqlParameters.size(); i++) {
//            statement.setString(i + 1, String.valueOf(sqlParameters.get(i).getValue()));
//        }
//
//        ResultSet resultSet = statement.executeQuery();
//
//        while (resultSet.next()) {
//            Map<String, String> column = new HashMap<>();
//            for (String fieldName : columnNames) {
//                column.put(fieldName, resultSet.getString(fieldName));
//            }
//            result.add(column);
//        }
//        return result;
//    }

    //    private boolean isTarget(Method method, Object executeResult) {
//        return isIntReturnType(method.getReturnType()) && isUpdated((int) executeResult);
//    }

    private String makeSql(String tableName, List<String> columnNames, List<SqlParameter> sqlParameters) {
        String conditionClause = sqlParameters.stream().map(k -> k.getName() + " = ?").collect(Collectors.joining(" AND "));
        String columns = String.join(", ", columnNames);
        return String.format("SELECT %s FROM %s WHERE %s", columns, tableName, conditionClause);
    }

    private List<Field> getIdFields(Class<?> entity) {
        List<Field> ids = Arrays.stream(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(AuditId.class))
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            throw new RuntimeException("There is no field with 'AuditId' annotation in the '" + entity.getSimpleName() + "'. [" + entity.getName() + "]");
        }
        return ids;
    }

    private List<String> makeColumnNames(Class<?> entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .map(field -> convertCamelToSnakeCase(field.getName()))
                .collect(Collectors.toList());
    }

    private String convertCamelToSnakeCase(String str) {
        String ret = str.replaceAll("([A-Z])", "_$1").replaceAll("([a-z][A-Z])", "$1_$2");
        return ret.toLowerCase();
    }

    private String convertSnakeToCamelCase(String str) {
        String[] fragments = str.toLowerCase().split("_");
        return fragments[0] + Arrays.stream(Arrays.copyOfRange(fragments, 1, fragments.length))
                .map(s -> StringUtils.isEmpty(s) ? s : s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining());
    }

    private boolean isIntReturnType(Object returnType) {
        return returnType == Integer.class || returnType == int.class;
    }

    private boolean isUpdated(int count) {
        return count >= 0;
    }

    private void assertHasAuditAnnotation(Method method) {
        if (method != null && method.isAnnotationPresent(Audit.class)) {
            return;
        }
        assert method != null;
        throw new RuntimeException("Method '" + method.getName() + "' has not 'Audit' annotation. [" + method.getDeclaringClass().getName() + "]");
    }

    private void assertHasAuditEntityAnnotation(Class<?> entity) {
        if (entity != null && entity.isAnnotationPresent(AuditEntity.class)) {
            return;
        }
        assert entity != null;
        throw new RuntimeException("Class '" + entity.getSimpleName() + "' has not 'AuditEntity' annotation. [" + entity.getName() + "]");
    }
}