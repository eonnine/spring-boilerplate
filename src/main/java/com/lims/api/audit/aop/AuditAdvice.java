package com.lims.api.audit.aop;

import com.lims.api.audit.context.AnnotationAuditMethodInvocation;
import com.lims.api.audit.context.AuditContainer;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.transaction.AuditTrailEventPublisher;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AuditAdvice implements MethodInterceptor {

    private final AuditContainer container;
    private final AuditSqlRepository repository;
    private final AuditTrailEventPublisher eventPublisher;

    public AuditAdvice(AuditContainer container, AuditSqlRepository repository, AuditTrailEventPublisher eventPublisher) {
        this.container = container;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        MethodInvocation auditInvocation = new AnnotationAuditMethodInvocation(invocation, container, repository, eventPublisher);
        return auditInvocation.proceed();
    }
}