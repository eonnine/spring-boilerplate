package com.lims.api.audit.event;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class DefaultAuditEventListener implements AuditEventListener {

    @Override
    public AspectJExpressionPointcut pointcut() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("*");
        return pointcut;
    }
}