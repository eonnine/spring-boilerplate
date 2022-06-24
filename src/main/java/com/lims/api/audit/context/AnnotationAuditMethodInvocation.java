package com.lims.api.audit.context;

import com.lims.api.audit.annotation.Audit;
import com.lims.api.audit.annotation.AuditEntity;
import com.lims.api.audit.annotation.AuditId;
import com.lims.api.audit.domain.AuditTrail;
import com.lims.api.audit.domain.SqlEntity;
import com.lims.api.audit.domain.SqlRow;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.transaction.AuditTransactionListener;
import com.lims.api.audit.transaction.AuditTransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationAuditMethodInvocation implements MethodInvocation {

    private final MethodInvocation target;

    private final AuditManager container;
    private final AuditSqlRepository repository;
    private final AuditTransactionListener transactionListener;

    public AnnotationAuditMethodInvocation(MethodInvocation invocation, AuditManager container, AuditSqlRepository repository, AuditTransactionListener transactionListener) {
        this.target = invocation;
        this.container = container;
        this.repository = repository;
        this.transactionListener = transactionListener;
    }

    @Override
    public Object proceed() throws Throwable {
        Method method = getMethod();
        Object[] args = getArguments();

        if (!AuditTransactionManager.isCurrentTransactionActive()) {
            initSynchronizeTransaction();
        }

        if (isPreSnapshotTarget(method)) {
            preSnapshot(method, args);
        }

        Object result = target.proceed();

        if (isPostSnapshotTarget(method, result)) {
            postSnapshot(method, args);
        }

        if (isCancelSnapshotTarget(method, result)) {
            cancelSnapshot();
        }

        return result;
    }

    private void initSynchronizeTransaction() {
        AuditTransactionManager.initCurrentTransaction();
        AuditTransactionManager.bindListener(transactionListener);
    }

    private void preSnapshot(Method method, Object[] args) {
        Audit auditAnnotation = getAuditAnnotation(method);
        Class<?> entityClazz = getAuditEntity(auditAnnotation);
        List<SqlRow> originData = getCurrentData(entityClazz, args);

        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setLabel(auditAnnotation.label());
        auditTrail.setContent(auditAnnotation.content());
        auditTrail.setOriginRows(originData);

        String transactionId = getCurrentTransactionId();
        container.put(transactionId, auditTrail);
    }

    private void postSnapshot(Method method, Object[] parameters) {
        Audit auditAnnotation = getAuditAnnotation(method);
        Class<?> entityClazz = getAuditEntity(auditAnnotation);
        List<SqlRow> updatedData = getCurrentData(entityClazz, parameters);

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

    private List<SqlRow> getCurrentData(Class<?> entityClazz, Object[] parameters) {
        return repository.findAllById(entityClazz, parameters);
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
        return AuditTransactionManager.getCurrentTransactionId();
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