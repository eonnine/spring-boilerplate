package com.lims.api.audit.aop;

import com.lims.api.audit.context.AnnotationAuditJoinPoint;
import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.transaction.AuditTransactionListener;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//@Aspect
//@Component
public class AuditAspect {
    private final AuditManager container;
    private final AuditSqlRepository repository;
    private final AuditTransactionListener transactionListener;

    public AuditAspect(AuditManager container, AuditSqlRepository repository, AuditTransactionListener transactionListener) {
        this.container = container;
        this.repository = repository;
        this.transactionListener = transactionListener;
    }

    @Pointcut("@annotation(com.lims.api.audit.annotation.Audit)")
    private void annotationPoint() {}

    @Around("annotationPoint()")
    public Object processing(ProceedingJoinPoint joinPoint) throws Throwable {
        ProceedingJoinPoint auditJoinPoint = new AnnotationAuditJoinPoint(joinPoint, container, repository, transactionListener);
        return auditJoinPoint.proceed();
    }
}