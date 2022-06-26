package com.lims.api.audit.aop;

import com.lims.api.audit.config.AuditBeanConfig;
import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.event.AuditTransactionListener;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.util.AuditAnnotationReader;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;

public class AuditAdvisor {
    private final AuditManager auditManager;
    private final AuditSqlRepository repository;
    private final AuditTransactionListener transactionListener;
    private final AuditAnnotationReader annotationReader;

    public AuditAdvisor() {
        AuditBeanConfig beanConfig = new AuditBeanConfig();
        this.auditManager = beanConfig.auditManager();
        this.repository = beanConfig.auditSqlRepository();
        this.transactionListener = beanConfig.auditTransactionListener();
        this.annotationReader = beanConfig.annotationReader();
    }

    @Bean
    public Advisor auditTrailAdvisor() {
        AspectJExpressionPointcut annotationPointcut = new AspectJExpressionPointcut();
        annotationPointcut.setExpression("@annotation(com.lims.api.audit.annotation.Audit)");
        return new DefaultPointcutAdvisor(annotationPointcut, new AuditAdvice(auditManager, repository, transactionListener, annotationReader));
    }

}