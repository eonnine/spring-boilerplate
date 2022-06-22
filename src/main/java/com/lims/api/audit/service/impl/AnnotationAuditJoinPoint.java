package com.lims.api.audit.service.impl;

import com.lims.api.audit.annotation.Audit;
import com.lims.api.audit.annotation.AuditEntity;
import com.lims.api.audit.annotation.AuditId;
import com.lims.api.audit.domain.AuditTrail;
import com.lims.api.audit.domain.SqlEntity;
import com.lims.api.audit.domain.SqlRow;
import com.lims.api.audit.service.AuditTrailConfigurer;
import com.lims.api.audit.service.SqlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationAuditJoinPoint extends MethodInvocationProceedingJoinPoint {

    private final AuditTrailConfigurer configurer;
    private final ProceedingJoinPoint target;
    private final AuditContainer container;
    private final AuditRepository repository;

    public AnnotationAuditJoinPoint(ProceedingJoinPoint joinPoint, AuditTrailConfigurer configurer, AuditContainer container, AuditRepository repository) {
        super((ProxyMethodInvocation) ExposeInvocationInterceptor.currentInvocation());
        this.target = joinPoint;
        this.configurer = configurer;
        this.container = container;
        this.repository = repository;
    }

    @Override
    public Object proceed() throws Throwable {
        MethodSignature signature = (MethodSignature) target.getSignature();
        Method method = signature.getMethod();
        Object[] args = target.getArgs();

        if (isPreSnapshotTarget(method)) {
            preSnapshot(method, args);
        }

        Object result = target.proceed();

        if (isPostSnapshotTarget(method, result)) {
            postSnapshot(method, args, result);
        }

        if (isCancelSnapshotTarget(method, result)) {
            cancelSnapshot();
        }

        return result;
    }

    private void preSnapshot(Method method, Object[] args) throws SQLException, NoSuchFieldException {
        Audit auditAnnotation = getAuditAnnotation(method);
        Class<?> entityClazz = getAuditEntity(auditAnnotation);
        List<SqlRow> originData = getCurrentData(entityClazz, method, args);

        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setLabel(auditAnnotation.label());
        auditTrail.setContent(auditAnnotation.content());
        auditTrail.setOriginRows(originData);

        String transactionId = getCurrentTransactionId();
        container.put(transactionId, auditTrail);
    }

    private void postSnapshot(Method method, Object[] parameters, Object result) throws SQLException, NoSuchFieldException {
        Audit auditAnnotation = getAuditAnnotation(method);
        Class<?> entityClazz = getAuditEntity(auditAnnotation);
        List<SqlRow> updatedData = getCurrentData(entityClazz, method, parameters);

        String transactionId = getCurrentTransactionId();
        List<AuditTrail> auditTrails = container.get(transactionId);

        AuditTrail auditTrail = auditTrails.get(auditTrails.size() - 1);
        auditTrail.setUpdatedRows(updatedData);
    }

    private void cancelSnapshot() {
        String transactionId = getCurrentTransactionId();
        if (container.has(transactionId)) {
            List<AuditTrail> auditTrails = container.get(transactionId);
            auditTrails.remove(auditTrails.size() - 1);
        }
    }

    private List<SqlRow> getCurrentData(Class<?> entityClazz, Method method, Object[] parameters) throws SQLException {
        try {
            AuditEntity entityAnnotation = entityClazz.getAnnotation(AuditEntity.class);
            String tableName = entityAnnotation.name();

            SqlEntity entity = new SqlEntity();
            entity.setName(tableName);
            entity.setTarget(entityClazz);
            entity.setIdFields(getIdFields(entityClazz));

            SqlGenerator generator = new OracleSqlGenerator(); // TODO 생성 패턴 적용
            return repository.findAllById(new SqlProvider(generator, entity), parameters);
        } catch(IllegalArgumentException e) {
            log.error("Arguments not found. [{}]", method.getName());
            throw e;
        }
    }

    private List<Field> getIdFields(Class<?> entityClazz) {
        List<Field> ids = Arrays.stream(entityClazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(AuditId.class))
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            throw new RuntimeException("There is no field with 'AuditId' annotation in the '" + entityClazz.getSimpleName() + "'. [" + entityClazz.getName() + "]");
        }
        return ids;
    }

    private String getCurrentTransactionId() {
        if (AuditTransactionManager.hasResourceCurrentTransaction()) {
            return AuditTransactionManager.getCurrentTransactionId();
        }
        return AuditTransactionManager.initResourceCurrentTransaction();
    }

    private Audit getAuditAnnotation(Method method) {
        assertHasAuditAnnotation(method);
        return method.getAnnotation(Audit.class);
    }

    private Class<?> getAuditEntity(Audit audit) {
        Class<?> clazz = audit.target();
        assertHasAuditEntityAnnotation(clazz);
        return clazz;
    }

    private boolean isPreSnapshotTarget(Method method) {
        return isTargetReturnType(method.getReturnType());
    }

    private boolean isPostSnapshotTarget(Method method, Object result) {
        return isPreSnapshotTarget(method) && isUpdated(result);
    }

    private boolean isCancelSnapshotTarget(Method method, Object result) {
        String transactionId = getCurrentTransactionId();
        boolean isExistsDiffData = false;
        if (container.has(transactionId)) {
            List<AuditTrail> auditTrails = container.get(transactionId);
            isExistsDiffData = auditTrails.stream().anyMatch(auditTrail -> auditTrail.isUpdated());
        }
        return isPreSnapshotTarget(method) && (!isUpdated(result) || !isExistsDiffData);
    }

    private boolean isTargetReturnType(Object returnType) {
        return returnType == Integer.class || returnType == int.class;
    }

    private boolean isUpdated(Object count) {
        return ((Integer) count) > 0;
    }

    private void assertHasAuditAnnotation(Method method) {
        if (method != null && method.isAnnotationPresent(Audit.class)) {
            return;
        }
        assert method != null;
        throw new RuntimeException("Method '" + method.getName() + "' has not 'Audit' annotation. [" + method.getDeclaringClass().getName() + "]");
    }

    private void assertHasAuditEntityAnnotation(Class<?> entityClazz) {
        if (entityClazz != null && entityClazz.isAnnotationPresent(AuditEntity.class)) {
            return;
        }
        assert entityClazz != null;
        throw new RuntimeException("Class '" + entityClazz.getSimpleName() + "' has not 'AuditEntity' annotation. [" + entityClazz.getName() + "]");
    }
}