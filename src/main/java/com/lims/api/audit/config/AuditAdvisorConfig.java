package com.lims.api.audit.config;

import com.lims.api.audit.aop.AuditAdvice;
import com.lims.api.audit.aop.AuditEventAdvice;
import com.lims.api.audit.context.AuditContainer;
import com.lims.api.audit.event.AuditEventListener;
import com.lims.api.audit.event.AuditEventPublisher;
import com.lims.api.audit.sql.AuditSqlRepository;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditAdvisorConfig {

    private final AuditContainer container;
    private final AuditSqlRepository repository;
    private final AuditEventPublisher eventPublisher;
    private final AuditEventListener eventListener;

    public AuditAdvisorConfig(AuditContainer container, AuditSqlRepository repository, AuditEventPublisher eventPublisher, AuditEventListener eventListener) {
        this.container = container;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.eventListener = eventListener;
    }

    @Bean
    public Advisor auditTrailAdvisor() {
        AspectJExpressionPointcut annotationPointcut = new AspectJExpressionPointcut();
        annotationPointcut.setExpression("@annotation(com.lims.api.audit.annotation.Audit)");
        return new DefaultPointcutAdvisor(annotationPointcut, new AuditAdvice(container, repository));
    }

    @Bean
    public Advisor auditTrailEventAdvisor() {
        return new DefaultPointcutAdvisor(eventListener.pointcut(), new AuditEventAdvice(container, eventPublisher));
    }

}