package com.lims.api.audit.aop;

import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.sql.AuditSqlRepository;
import com.lims.api.audit.event.AuditTransactionListener;
import com.lims.api.audit.util.AuditAnnotationReader;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditAdvisor {

    private final AuditManager auditManager;
    private final AuditSqlRepository repository;
    private final AuditAnnotationReader annotationReader;
    private final AuditTransactionListener transactionListener;

    public AuditAdvisor(AuditManager auditManager, AuditSqlRepository repository, AuditAnnotationReader auditAnnotationReader,
                        AuditTransactionListener transactionListener) {
        this.auditManager = auditManager;
        this.repository = repository;
        this.annotationReader = auditAnnotationReader;
        this.transactionListener = transactionListener;
    }

    @Bean
    public Advisor auditTrailAdvisor() {
        AspectJExpressionPointcut annotationPointcut = new AspectJExpressionPointcut();
        annotationPointcut.setExpression("@annotation(com.lims.api.audit.annotation.Audit)");
        return new DefaultPointcutAdvisor(annotationPointcut, new AuditAdvice(auditManager, repository, annotationReader, transactionListener));
    }

}