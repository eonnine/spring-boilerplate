package com.lims.api.audit.event;

import com.lims.api.audit.domain.AuditString;
import com.lims.api.audit.domain.AuditTrail;
import com.lims.api.audit.config.AuditTrailConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuditEventPublisher {

    private final AuditEventListener eventListener;
    private final AuditTrailConfigurer configurer;

    public AuditEventPublisher(AuditEventListener eventListener, AuditTrailConfigurer configurer) {
        this.eventListener = eventListener;
        this.configurer = configurer;
    }

    public void publishBeforeCommit(List<AuditTrail> auditTrails) {
        System.out.println(TransactionSynchronizationManager.getSynchronizations().size());
        List<AuditString> auditStrings = auditTrails.stream()
                .map(auditTrail -> auditTrail.toAuditString(configurer))
                .collect(Collectors.toList());

        eventListener.beforeCommit(auditStrings);
    }
}