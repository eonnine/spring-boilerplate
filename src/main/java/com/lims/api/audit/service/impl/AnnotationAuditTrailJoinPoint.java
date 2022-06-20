package com.lims.api.audit.service.impl;

import com.lims.api.audit.annotation.Audit;
import com.lims.api.audit.annotation.AuditEntity;
import com.lims.api.audit.annotation.AuditId;
import com.lims.api.audit.domain.SqlColumn;
import com.lims.api.audit.domain.SqlParameter;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationAuditTrailJoinPoint {

    private AuditTrailRepository repository;
    private ProceedingJoinPoint target;

    public AnnotationAuditTrailJoinPoint(ProceedingJoinPoint joinPoint, AuditTrailRepository repository) {
        this.target = joinPoint;
        this.repository = repository;
    }

    public Object proceed() throws Throwable {
        Method method = getMethod();

        if (isIntReturnType(method)) {
            preHandle();
        }

        Object result = target.proceed();

        if (isIntReturnType(method) && isUpdated((Integer) result)) {
            postHandle();
        }

        return result;
    }

    private Method getMethod() {
        MethodSignature signature = (MethodSignature) target.getSignature();
        return signature.getMethod();
    }

    private Object getParameter() {
        if (target.getArgs().length == 0) {
            throw new IllegalArgumentException("Parameter not found. [" + getMethod().getName()  + "]");
        }
        return target.getArgs()[0];
    }

    private void preHandle() throws NoSuchFieldException, SQLException {
        Method method = getMethod();

        assertHasAuditAnnotation(method);

        Audit auditAnnotation = method.getAnnotation(Audit.class);
        Class<?> entity = auditAnnotation.target();

        assertHasAuditEntityAnnotation(entity);

        AuditEntity entityAnnotation = entity.getAnnotation(AuditEntity.class);
        String tableName = entityAnnotation.name();
        List<String> columnNames = makeColumnNames(entity);
        List<SqlParameter> parameters = getSqlParameter(entity);

        String sql = repository.makeSelectSql(tableName, columnNames, parameters);
        List<SqlColumn> originData = repository.findAllById(sql, parameters);
    }

    private void postHandle() {
        // TODO 체크 문자열 작성 (old data 기준으로 비교)

        // TODO (grouping 설정에 따라 병합해서 insert or 개별 insert
    }

    private List<SqlParameter> getSqlParameter(Class<?> entity) throws NoSuchFieldException {
        List<Field> idFields = getIdFields(entity);
        Object parameter = getParameter();
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