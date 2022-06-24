package com.lims.api.audit.config;

import com.lims.api.audit.transaction.AuditEventListener;
import com.lims.api.audit.transaction.DefaultAuditEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    private final AuditProperties auditProperties;

    public AuditConfig(AuditProperties auditProperties) {
        this.auditProperties = auditProperties;
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

}