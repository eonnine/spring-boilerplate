package com.lims.api.audit.config;

import com.lims.api.audit.aop.AuditTrailAdvice;
import com.lims.api.audit.aop.AuditTrailEventAdvice;
import com.lims.api.audit.context.AuditContainer;
import com.lims.api.audit.event.AuditEventPublisher;
import com.lims.api.audit.sql.AuditSqlRepository;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditTrailAdvisorConfig {

    private final AuditContainer container;
    private final AuditSqlRepository repository;
    private final AuditEventPublisher eventPublisher;

    public AuditTrailAdvisorConfig(AuditContainer container, AuditSqlRepository repository, AuditEventPublisher eventPublisher) {
        this.container = container;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Bean
    public Advisor auditTrailAdvisor() {
        AspectJExpressionPointcut annotationPointcut = new AspectJExpressionPointcut();
        annotationPointcut.setExpression("@annotation(com.lims.api.audit.annotation.Audit)");
        return new DefaultPointcutAdvisor(annotationPointcut, new AuditTrailAdvice(container, repository));
    }

    @Bean
    public Advisor auditTrailEventAdvisor() {
        AspectJExpressionPointcut annotationPointcut = new AspectJExpressionPointcut();
        annotationPointcut.setExpression("@annotation(com.lims.api.audit.annotation.Audit)");
        return new DefaultPointcutAdvisor(annotationPointcut, new AuditTrailEventAdvice(container, eventPublisher));
    }

}