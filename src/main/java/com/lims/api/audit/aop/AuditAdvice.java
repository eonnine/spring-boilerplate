package com.lims.api.audit.aop;

import com.lims.api.audit.context.AnnotationAuditMethodInvocation;
import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.event.AuditTransactionListener;
import com.lims.api.audit.util.AuditAnnotationReader;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AuditAdvice implements MethodInterceptor {

    private final AuditManager auditManager;
    private final AuditSqlRepository repository;
    private final AuditAnnotationReader annotationReader;
    private final AuditTransactionListener transactionListener;

    public AuditAdvice(AuditManager auditManager, AuditSqlRepository repository, AuditAnnotationReader annotationReader,
                       AuditTransactionListener transactionListener) {
        this.auditManager = auditManager;
        this.repository = repository;
        this.annotationReader = annotationReader;
        this.transactionListener = transactionListener;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        MethodInvocation auditInvocation = new AnnotationAuditMethodInvocation(invocation, auditManager, repository, annotationReader, transactionListener);
        return auditInvocation.proceed();
    }
}