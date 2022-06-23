package com.lims.api.audit.config;

import com.lims.api.audit.event.DefaultAuditEventListener;
import com.lims.api.audit.event.AuditEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    @Bean
    @ConditionalOnMissingBean
    public AuditTrailConfigurer auditTrailConfigurer() {
        return new DefaultAuditTrailConfigurer();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditEventListener auditEventListener() {
        return new DefaultAuditEventListener();
    }

}