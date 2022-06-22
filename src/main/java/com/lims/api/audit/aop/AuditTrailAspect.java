package com.lims.api.audit.aop;

import com.lims.api.audit.service.impl.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
public class AuditTrailAspect {

    private final AuditContainer container;
    private final AuditRepository repository;
    private final AuditEventPublisher eventPublisher;

    public AuditTrailAspect(AuditContainer container, AuditRepository repository, AuditEventPublisher eventPublisher) {
        this.container = container;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Pointcut("execution(* com.lims.api..dao.*.*(..))")
    private void repositoryPoint() {}

    @Pointcut("@annotation(com.lims.api.audit.annotation.Audit)")
    private void annotationPoint() {}

    @Pointcut("execution(* com.lims.api..service.*.*(..))")
    private void servicePoint() {}

    @Around("repositoryPoint() && annotationPoint()")
    public Object processing(ProceedingJoinPoint joinPoint) throws Throwable {
        ProceedingJoinPoint auditJoinPoint = new AnnotationAuditJoinPoint(joinPoint, container, repository);
        return auditJoinPoint.proceed();
    }

    @Before("servicePoint()")
    public void transactionListener(JoinPoint joinPoint) throws Throwable {
        TransactionSynchronizationManager.registerSynchronization(new AuditTransactionListener(container, eventPublisher));
    }
}