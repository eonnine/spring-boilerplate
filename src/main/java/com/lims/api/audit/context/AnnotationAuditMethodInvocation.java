package com.lims.api.audit.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.api.audit.annotation.Audit;
import com.lims.api.audit.domain.AuditAttribute;
import com.lims.api.audit.domain.SqlParameter;
import com.lims.api.audit.domain.SqlRow;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.event.AuditTransactionListener;
import com.lims.api.audit.event.AuditTransactionManager;
import com.lims.api.audit.util.AuditAnnotationReader;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationAuditMethodInvocation implements MethodInvocation {

    private final MethodInvocation target;

    private final AuditManager auditManager;
    private final AuditSqlRepository repository;
    private final AuditAnnotationReader annotationReader;
    private final AuditTransactionListener transactionListener;

    public AnnotationAuditMethodInvocation(MethodInvocation invocation, AuditManager auditManager, AuditSqlRepository repository,
                                           AuditAnnotationReader annotationReader, AuditTransactionListener transactionListener) {
        this.target = invocation;
        this.auditManager = auditManager;
        this.repository = repository;
        this.annotationReader = annotationReader;
        this.transactionListener = transactionListener;
    }

    @Override
    public Object proceed() throws Throwable {
        if (!AuditTransactionManager.isCurrentTransactionActive()) {
            initSynchronizeTransaction();
        }

        Method method = getMethod();

        if (!isInteger(method.getReturnType())) {
            return target.proceed();
        }

        Object[] args = getArguments();
        Audit auditAnnotation = annotationReader.getAuditAnnotation(method);
        Class<?> entityClazz = annotationReader.getAuditEntity(auditAnnotation);

        Map<String, Object> auditParameter = convertArgsToMap(entityClazz, args);
        List<SqlParameter> sqlParameters = getSqlParameter(entityClazz, auditParameter);
        Map<String, Object> entityId = getEntityId(sqlParameters);
        String auditKey = generateAuditKey(entityId, entityClazz);

        if (isPreSnapshotTarget(auditKey)) {
            preSnapshot(auditKey, entityClazz, entityId, sqlParameters, auditParameter, auditAnnotation);
        }

        Object result = target.proceed();

        if (isPostSnapshotTarget(result)) {
            postSnapshot(auditKey, entityClazz, sqlParameters, args);
        }

        return result;
    }

    private void initSynchronizeTransaction() {
        AuditTransactionManager.initCurrentTransaction();
        AuditTransactionManager.bindListener(transactionListener);
    }

    private void preSnapshot(String auditKey, Class<?> entityClazz, Map<String, Object> entityId,
                             List<SqlParameter> sqlParameters, Map<String, Object> auditParameter, Audit auditAnnotation) {
        List<SqlRow> originData = repository.findAllById(entityClazz, sqlParameters);

        AuditAttribute auditTrail = new AuditAttribute();
        auditTrail.setLabel(auditAnnotation.label());
        auditTrail.setContent(auditAnnotation.content());
        auditTrail.setOriginRows(originData);
        auditTrail.setId(entityId);
        auditTrail.setParameter(auditParameter);

        auditManager.put(auditKey, auditTrail);
    }

    private void postSnapshot(String auditKey, Class<?> entityClazz, List<SqlParameter> sqlParameters, Object[] args) {
        AuditAttribute auditTrail = auditManager.get(auditKey);

        if (auditTrail.getCommandType().isInsert()) {
            sqlParameters = getSqlParameter(entityClazz, convertArgsToMap(entityClazz, args));
        }

        List<SqlRow> updatedData = repository.findAllById(entityClazz, sqlParameters);
        auditTrail.setUpdatedRows(updatedData);
    }

    private boolean isPreSnapshotTarget(String key) {
        return !auditManager.has(key);
    }

    private boolean isPostSnapshotTarget(Object result) {
        return isUpdated(result);
    }

    private boolean isInteger(Object v) {
        return v == Integer.class || v == int.class;
    }

    private boolean isUpdated(Object v) {
        return v instanceof Integer && ((Integer) v) > 0;
    }

    private List<SqlParameter> getSqlParameter(Class<?> entityClazz, Map<String,Object> parameter) {
        return repository.getSqlParameters(entityClazz, parameter);
    }

    private Map<String, Object> getEntityId(List<SqlParameter> sqlParameters) {
        Map<String, Object> parameterMap = new HashMap<>();
        for (SqlParameter sp : sqlParameters) {
            parameterMap.put(sp.getName(), sp.getData());
        }
        return parameterMap;
    }

    private String generateAuditKey(Map<String, Object> parameterMap, Class<?> entityClazz) {
        String separator = "$";
        String paramValue = parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(separator));
        return entityClazz.getName() + separator + paramValue;
    }

    private Map<String, Object> convertArgsToMap(Class<?> entityClazz, Object[] args) {
        assertExistsParameter(args);
        Object arg = args[0];
        if (arg instanceof String || arg instanceof Integer) {
            return convertParamToMap(entityClazz, arg);
        }
        return convertObjectToMap(arg);
    }

    private Map<String, Object> convertParamToMap(Class<?> entityClazz, Object arg) {
        String value = String.valueOf(arg);
        List<String> fieldNames = annotationReader.getIdFieldNames(entityClazz);
        if (fieldNames.size() > 1) {
            throw new RuntimeException("The parameter '" + value + "' can't be matched bacause there is more than one id field in the '" + entityClazz.getSimpleName() + "'. [" + entityClazz.getName() + "]");
        }
        return fieldNames.stream().collect(Collectors.toMap(s -> s, s -> value));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertObjectToMap(Object arg) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(arg, Map.class);
    }

    private void assertExistsParameter(Object[] parameters) {
        if (parameters.length > 0) {
            return;
        }
        throw new IllegalArgumentException("Not found sql parameters. Parameter is required. [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
    }

    @Override
    public Method getMethod() {
        return target.getMethod();
    }

    @Override
    public Object[] getArguments() {
        return target.getArguments();
    }

    @Override
    public Object getThis() {
        return target.getThis();
    }

    @Override
    public AccessibleObject getStaticPart() {
        return target.getStaticPart();
    }
}