package com.lims.api.audit.config;

import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.context.AuditManagerFactory;
import com.lims.api.audit.sql.AuditSqlGenerator;
import com.lims.api.audit.sql.AuditSqlGeneratorFactory;
import com.lims.api.audit.event.AuditEventListener;
import com.lims.api.audit.event.DefaultAuditEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditBeanConfig {

    private final AuditProperties auditProperties;
    private final AuditConfigurer configurer;

    public AuditBeanConfig(AuditProperties auditProperties, AuditConfigurer configurer) {
        this.auditProperties = auditProperties;
        this.configurer = configurer;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditConfigurer auditTrailConfigurer() {
        return new DefaultAuditConfigurer(auditProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditEventListener auditTrailEventListener() {
        return new DefaultAuditEventListener();
    }

    @Bean
    public AuditManager auditManager() {
        return new AuditManagerFactory(configurer).create();
    }

    @Bean
    public AuditSqlGenerator auditSqlGenerator() {
        return new AuditSqlGeneratorFactory(configurer).create();
    }

}