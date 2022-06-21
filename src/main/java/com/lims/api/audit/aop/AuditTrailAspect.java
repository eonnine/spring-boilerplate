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

    public AuditTrailAspect(AuditContainer container, AuditRepository repository) {
        this.container = container;
        this.repository = repository;
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
        TransactionSynchronizationManager.registerSynchronization(new AuditTransactionListener(container, repository));
    }

    /**
     * TODO
     * - 예외 처리
     * 예외 발생시 예외가 발생한 Dao & method name 출력
     *
     * - option 설정
     * 1. grouping이면 store에 그룹핑
     *  -> grouping이면 병합해서 insert
     *  -> no grouping이면 row별로 insert
     *
     *
     * 3. camel <-> snake
     */
}