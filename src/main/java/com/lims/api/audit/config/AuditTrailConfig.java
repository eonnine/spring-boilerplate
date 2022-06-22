package com.lims.api.audit.config;

import com.lims.api.audit.service.AuditEventListener;
import com.lims.api.audit.service.AuditTrailConfigurer;
import com.lims.api.audit.service.impl.DefaultAuditEventListener;
import com.lims.api.audit.service.impl.DefaultAuditTrailConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditTrailConfig {

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