package com.lims.api.audit.aop;

import com.lims.api.audit.context.AnnotationAuditJoinPoint;
import com.lims.api.audit.context.AuditContainer;
import com.lims.api.audit.sql.AuditSqlRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

//@Aspect
//@Component
public class AuditAspect {
    private final AuditContainer container;
    private final AuditSqlRepository repository;

    public AuditAspect(AuditContainer container, AuditSqlRepository repository) {
        this.container = container;
        this.repository = repository;
    }

    @Pointcut("execution(* com.lims.api..dao.*.*(..))")
    private void repositoryPoint() {}

    @Pointcut("@annotation(com.lims.api.audit.annotation.Audit)")
    private void annotationPoint() {}

    @Around("repositoryPoint() && annotationPoint()")
    public Object processing(ProceedingJoinPoint joinPoint) throws Throwable {
        ProceedingJoinPoint auditJoinPoint = new AnnotationAuditJoinPoint(joinPoint, container, repository);
        return auditJoinPoint.proceed();
    }
}