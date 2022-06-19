package com.lims.api.audit.aop;

import com.lims.api.audit.domain.AuditTrail;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditTrailAspect {

    private final AuditTrail auditTrail;

    @Pointcut("execution(* com.lims.api..dao.*.*(..))")
    private void executionPoint() {}

    @Pointcut("@annotation(com.lims.api.audit.annotation.Audit)")
    private void annotationPoint() {}

    /**
     * TODO
     * 예외 발생시 예외가 발생한 Dao & method name 출력
     */

    @Around("executionPoint() && annotationPoint()")
    public Object AuditTrail(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object result = joinPoint.proceed();
        auditTrail.record(signature.getMethod(), joinPoint.getArgs(), result);
        return result;
    }

}
