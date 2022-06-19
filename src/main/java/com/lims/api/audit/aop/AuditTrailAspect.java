package com.lims.api.audit.aop;

import com.lims.api.audit.domain.AnnotationAuditTrail;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Aspect
@Component
public class AuditTrailAspect {

    private final DataSource dataSource;

    public AuditTrailAspect(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Pointcut("execution(* com.lims.api..dao.*.*(..))")
    private void executionPoint() {}

    @Pointcut("@annotation(com.lims.api.audit.annotation.Audit)")
    private void annotationPoint() {}

    /**
     * TODO
     * 예외 발생시 예외가 발생한 Dao & method name 출력
     */

    /**
     * process
     * - Transaction 시작시 store 생성
     * - grouping이면 store에 그룹핑
     * -> grouping이면 병합해서 insert
     * -> no grouping이면 row별로 insert
     */

    @Around("executionPoint() && annotationPoint()")
    public Object AuditTrail(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AnnotationAuditTrail auditTrail = new AnnotationAuditTrail(dataSource, signature.getMethod(), getParameter(joinPoint.getArgs()), joinPoint::proceed);
        return auditTrail.record();
    }

    private Object getParameter(Object[] args) {
        return args.length > 0 ? args[0] : null;
    }

}