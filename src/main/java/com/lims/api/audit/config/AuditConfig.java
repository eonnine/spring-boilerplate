package com.lims.api.audit.config;

import com.lims.api.audit.transaction.AuditTrailEventListener;
import com.lims.api.audit.transaction.DefaultAuditTransactionListener;
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
    public AuditTrailEventListener auditTrailEventListener() {
        return new DefaultAuditTransactionListener();
    }

}