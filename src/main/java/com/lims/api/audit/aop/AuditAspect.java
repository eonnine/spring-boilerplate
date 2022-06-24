package com.lims.api.audit.aop;

import com.lims.api.audit.context.AnnotationAuditJoinPoint;
import com.lims.api.audit.context.AuditContainer;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.transaction.AuditTrailEventPublisher;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {
    private final AuditContainer container;
    private final AuditSqlRepository repository;
    private final AuditTrailEventPublisher eventPublisher;

    public AuditAspect(AuditContainer container, AuditSqlRepository repository, AuditTrailEventPublisher eventPublisher) {
        this.container = container;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Pointcut("@annotation(com.lims.api.audit.annotation.Audit)")
    private void annotationPoint() {}

    @Around("annotationPoint()")
    public Object processing(ProceedingJoinPoint joinPoint) throws Throwable {
        ProceedingJoinPoint auditJoinPoint = new AnnotationAuditJoinPoint(joinPoint, container, repository, eventPublisher);
        return auditJoinPoint.proceed();
    }
}