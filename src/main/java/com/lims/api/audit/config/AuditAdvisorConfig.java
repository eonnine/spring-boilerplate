package com.lims.api.audit.config;

import com.lims.api.audit.aop.AuditAdvice;
import com.lims.api.audit.context.AuditContainer;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.transaction.AuditTrailEventListener;
import com.lims.api.audit.transaction.AuditTrailEventPublisher;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;

//@Configuration
public class AuditAdvisorConfig {

    private final AuditContainer container;
    private final AuditSqlRepository repository;
    private final AuditTrailEventPublisher eventPublisher;

    public AuditAdvisorConfig(AuditContainer container, AuditSqlRepository repository, AuditTrailEventPublisher eventPublisher, AuditTrailEventListener eventListener) {
        this.container = container;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Bean
    public Advisor auditTrailAdvisor() {
        AspectJExpressionPointcut annotationPointcut = new AspectJExpressionPointcut();
        annotationPointcut.setExpression("@annotation(com.lims.api.audit.annotation.Audit)");
        return new DefaultPointcutAdvisor(annotationPointcut, new AuditAdvice(container, repository, eventPublisher));
    }

}