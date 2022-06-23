package com.lims.api.config;

import com.lims.api.audit.domain.AuditString;
import com.lims.api.audit.event.AuditEventListener;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.context.annotation.Configuration;

import java.util.List;

//@Configuration
public class AuditTrailEventListener implements AuditEventListener {

    @Override
    public AspectJExpressionPointcut pointcut() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.lims.api..service.*.*(..))");
        return pointcut;
    }

    @Override
    public void beforeCommit(List<AuditString> auditTrail) {
        auditTrail.forEach(auditString -> {
            System.out.println(auditString.getDiffString());
            System.out.println(auditString.getLabel());
            System.out.println(auditString.getContent());
        });
    }
}