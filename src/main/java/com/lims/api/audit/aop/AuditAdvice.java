package com.lims.api.audit.aop;

import com.lims.api.audit.context.AnnotationAuditMethodInvocation;
import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.transaction.AuditTransactionListener;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AuditAdvice implements MethodInterceptor {

    private final AuditManager container;
    private final AuditSqlRepository repository;
    private final AuditTransactionListener transactionListener;

    public AuditAdvice(AuditManager container, AuditSqlRepository repository, AuditTransactionListener transactionListener) {
        this.container = container;
        this.repository = repository;
        this.transactionListener = transactionListener;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        MethodInvocation auditInvocation = new AnnotationAuditMethodInvocation(invocation, container, repository, transactionListener);
        return auditInvocation.proceed();
    }
}