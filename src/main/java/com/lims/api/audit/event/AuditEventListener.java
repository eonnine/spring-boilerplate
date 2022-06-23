package com.lims.api.audit.event;

import com.lims.api.audit.domain.AuditString;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.util.List;

public interface AuditEventListener {

    AspectJExpressionPointcut pointcut();

    default void beforeCommit(List<AuditString> auditTrail) {};

}