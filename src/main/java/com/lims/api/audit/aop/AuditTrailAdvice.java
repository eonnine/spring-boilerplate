package com.lims.api.audit.aop;

import com.lims.api.audit.context.AnnotationAuditMethodInvocation;
import com.lims.api.audit.context.AuditContainer;
import com.lims.api.audit.sql.AuditSqlRepository;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AuditTrailAdvice implements MethodInterceptor {

    private final AuditContainer container;
    private final AuditSqlRepository repository;

    public AuditTrailAdvice(AuditContainer container, AuditSqlRepository repository) {
        this.container = container;
        this.repository = repository;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        MethodInvocation auditInvocation = new AnnotationAuditMethodInvocation(invocation, container, repository);
        return auditInvocation.proceed();
    }
}