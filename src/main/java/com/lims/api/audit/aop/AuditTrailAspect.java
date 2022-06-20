package com.lims.api.audit.aop;

import com.lims.api.audit.service.impl.AnnotationAuditTrailJoinPoint;
import com.lims.api.audit.service.impl.AuditTrailContainer;
import com.lims.api.audit.service.impl.AuditTrailRecorder;
import com.lims.api.audit.service.impl.AuditTrailRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;

@Aspect
@Component
public class AuditTrailAspect {

    private final DataSource dataSource;
    private final AuditTrailContainer container;

    public AuditTrailAspect(DataSource dataSource, AuditTrailContainer container) {
        this.dataSource = dataSource;
        this.container = container;
    }

    @Pointcut("execution(* com.lims.api..dao.*.*(..))")
    private void executionPoint() {}

    @Pointcut("@annotation(com.lims.api.audit.annotation.Audit)")
    private void annotationPoint() {}

    @Pointcut("execution(* com.lims.api..service.*.*(..))")
    private void servicePoint() {}

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
        return new AnnotationAuditTrailJoinPoint(joinPoint, new AuditTrailRepository(dataSource)).proceed();
    }


    @Before("servicePoint()")
    public void Transaction(JoinPoint joinPoint) throws Throwable {
        System.out.println(joinPoint);
        TransactionSynchronizationManager.registerSynchronization(new AuditTrailRecorder(container));
    }

}