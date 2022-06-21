package com.lims.api.audit.service.impl;

import com.lims.api.audit.annotation.Audit;
import com.lims.api.audit.annotation.AuditEntity;
import com.lims.api.audit.annotation.AuditId;
import com.lims.api.audit.domain.AuditTrail;
import com.lims.api.audit.domain.SqlRow;
import com.lims.api.audit.domain.SqlParameter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationAuditTrailContext {

    private final AuditTrailContainer container;
    private final AuditTrailRepository repository;
    private final ProceedingJoinPoint target;

    public AnnotationAuditTrailContext(ProceedingJoinPoint joinPoint, AuditTrailContainer container, AuditTrailRepository repository) {
        this.target = joinPoint;
        this.container = container;
        this.repository = repository;
    }

    public Object proceed() throws Throwable {
        Method method = getMethod();

        if (isIntReturnType(method.getReturnType())) {
            preHandle();
        }

        Object result = target.proceed();

        if (isIntReturnType(method.getReturnType())) {
            postHandle(result);
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
        List<SqlRow> originData = repository.findAllById(sql, parameters);

        String transactionId;
        if (AuditTrailTransaction.hasResourceCurrentTransaction()) {
            transactionId = AuditTrailTransaction.getCurrentTransactionId();
        } else {
            transactionId = AuditTrailTransaction.initResourceCurrentTransaction();
        }

        AuditTrail auditTrail = AuditTrail.builder()
                .type(auditAnnotation.type())
                .label(auditAnnotation.label())
                .content(auditAnnotation.content())
                .origin(originData)
                .build();

        container.put(transactionId, auditTrail);
    }

    private void postHandle(Object updatedResult) throws NoSuchFieldException, SQLException {
        // TODO 체크 문자열 작성 (old data 기준으로 비교(= entity 필드 기준으로))
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
        List<SqlRow> updatedData = repository.findAllById(sql, parameters);

        String transactionId;
        if (AuditTrailTransaction.hasResourceCurrentTransaction()) {
            transactionId = AuditTrailTransaction.getCurrentTransactionId();
        } else {
            transactionId = AuditTrailTransaction.initResourceCurrentTransaction();
        }

        List<AuditTrail> auditTrails = container.get(transactionId);

        AuditTrail auditTrail = auditTrails.get(auditTrails.size() - 1);
        auditTrail.setUpdatedRows(updatedData);
        auditTrail.setUpdated(isUpdated((Integer) updatedResult));
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