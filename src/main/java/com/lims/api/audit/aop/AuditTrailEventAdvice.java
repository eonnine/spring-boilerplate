package com.lims.api.audit.aop;

import com.lims.api.audit.implementz.AuditContainer;
import com.lims.api.audit.event.AuditEventPublisher;
import com.lims.api.audit.transaction.AuditTransactionListener;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class AuditTrailEventAdvice implements MethodInterceptor {

    private final AuditContainer container;
    private final AuditEventPublisher eventPublisher;

    public AuditTrailEventAdvice(AuditContainer container, AuditEventPublisher eventPublisher) {
        this.container = container;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionSynchronizationManager.registerSynchronization(new AuditTransactionListener(container, eventPublisher));
        return invocation.proceed();
    }

}