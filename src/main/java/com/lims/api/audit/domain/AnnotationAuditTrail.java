package com.lims.api.audit.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.lims.api.audit.annotation.Audit;
import com.lims.api.audit.annotation.AuditEntity;
import com.lims.api.audit.annotation.AuditId;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.util.StringUtil;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnotationAuditTrail implements AuditTrail {

    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public void record(Method method, Object[] args, Object executeResult) throws SQLException, NoSuchFieldException {
        if (!isTarget(method, executeResult)) {
            return;
        }

        assertHasAuditAnnotation(method);

        Audit auditAnnotation = method.getAnnotation(Audit.class);
        Class<?> entity = auditAnnotation.target();

        assertHasAuditEntityAnnotation(entity);

        AuditEntity entityAnnotation = entity.getAnnotation(AuditEntity.class);
        String tableName = entityAnnotation.name();
        List<String> columnNames = makeColumnNames(entity);
        List<Field> idFields = getIdFields(entity);

        Object arg = args[0];
        Class<?> parameterClazz = arg.getClass();
        LinkedHashMap<String, Object> sqlParameters = new LinkedHashMap<>();
        try {
            for (Field field : idFields) {
                Field parameterField = parameterClazz.getDeclaredField(field.getName());
                parameterField.setAccessible(true);
                Object value = parameterField.get(arg);
                sqlParameters.put(field.getName(), value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new NoSuchFieldException("Parameter '" + parameterClazz.getName() + "' has not a field matching the id field in the audit entity '" + entity.getName() + "'. [" + e.getMessage() + "]");
        }

        executeQuery(tableName, columnNames, sqlParameters);
    }

    private void executeQuery(String tableName, List<String> columnNames, LinkedHashMap<String, Object> sqlParameters) throws SQLException {
        String columns = String.join(", ", columnNames);
        String sql = String.format("select %s from %s", columns, tableName);

        try (
                SqlSession sqlSession = sqlSessionFactory.openSession();
                Connection connection = sqlSession.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {

            ResultSet resultSet = statement.executeQuery();

//            while (resultSet.next()) {
//                for (String fieldName : columnNames) {
//                    System.out.println(resultSet.getString(fieldName));
//                }
//            }
        }
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

    private boolean isTarget(Method method, Object executeResult) {
        return isIntReturnType(method.getReturnType()) && isUpdated((int) executeResult);
    }

    private boolean isIntReturnType(Object returnType) {
        return returnType == Integer.class || returnType == int.class;
    }

    private boolean isUpdated(int count) {
        return count > 0;
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