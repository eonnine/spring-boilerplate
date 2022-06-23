package com.lims.api.audit.aop;

import com.lims.api.audit.implementz.AuditContainer;
import com.lims.api.audit.event.AuditEventPublisher;
import com.lims.api.audit.transaction.AuditTransactionListener;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.transaction.support.TransactionSynchronizationManager;

//@Aspect
//@Component
public class AuditTrailEventAspect {
    private final AuditContainer container;
    private final AuditEventPublisher eventPublisher;

    public AuditTrailEventAspect(AuditContainer container, AuditEventPublisher eventPublisher) {
        this.container = container;
        this.eventPublisher = eventPublisher;
    }

    @Pointcut("execution(* com.lims.api..service.*.*(..))")
    private void servicePoint() {};

    @Before("servicePoint()")
    public void transactionListener() {
        TransactionSynchronizationManager.registerSynchronization(new AuditTransactionListener(container, eventPublisher));
    }
}