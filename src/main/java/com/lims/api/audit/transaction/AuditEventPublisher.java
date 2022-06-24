package com.lims.api.audit.transaction;

import com.lims.api.audit.config.AuditConfigurer;
import com.lims.api.audit.domain.AuditString;
import com.lims.api.audit.domain.AuditTrail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuditEventPublisher {

    private final AuditEventListener eventListener;
    private final AuditConfigurer configurer;

    public AuditEventPublisher(AuditEventListener eventListener, AuditConfigurer configurer) {
        this.eventListener = eventListener;
        this.configurer = configurer;
    }

    public void publishBeforeCommit(List<AuditTrail> auditTrails) {
        eventListener.beforeCommit(convertToAuditString(auditTrails));
    }

    public void publishAfterCommit(List<AuditTrail> auditTrails) {
        eventListener.afterCommit(convertToAuditString(auditTrails));
    }

    private List<AuditString> convertToAuditString(List<AuditTrail> auditTrails) {
        return auditTrails.stream()
                .map(auditTrail -> auditTrail.toAuditString(configurer))
                .collect(Collectors.toList());
    }
}