package com.lims.api.audit.config;

import com.lims.api.audit.transaction.AuditEventListener;
import com.lims.api.audit.transaction.DefaultAuditEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    @Bean
    @ConditionalOnMissingBean
    public AuditConfigurer auditTrailConfigurer() {
        return new DefaultAuditConfigurer();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditEventListener auditTrailEventListener() {
        return new DefaultAuditEventListener();
    }

}