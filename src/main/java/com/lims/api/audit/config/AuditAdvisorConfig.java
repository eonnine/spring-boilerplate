package com.lims.api.audit.config;

import com.lims.api.audit.aop.AuditAdvice;
import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.transaction.AuditTransactionListener;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditAdvisorConfig {

    private final AuditManager container;
    private final AuditSqlRepository repository;
    private final AuditTransactionListener transactionListener;

    public AuditAdvisorConfig(AuditManager container, AuditSqlRepository repository, AuditTransactionListener transactionListener) {
        this.container = container;
        this.repository = repository;
        this.transactionListener = transactionListener;
    }

    @Bean
    public Advisor auditTrailAdvisor() {
        AspectJExpressionPointcut annotationPointcut = new AspectJExpressionPointcut();
        annotationPointcut.setExpression("@annotation(com.lims.api.audit.annotation.Audit)");
        return new DefaultPointcutAdvisor(annotationPointcut, new AuditAdvice(container, repository, transactionListener));
    }

}