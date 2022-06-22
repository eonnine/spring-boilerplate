package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.AuditString;
import com.lims.api.audit.domain.AuditTrail;
import com.lims.api.audit.service.AuditEventListener;
import com.lims.api.audit.service.AuditTrailConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuditEventPublisher {

    private AuditEventListener eventListener;
    private final AuditTrailConfigurer configurer;

    public AuditEventPublisher(AuditEventListener eventListener, AuditTrailConfigurer configurer) {
        this.eventListener = eventListener;
        this.configurer = configurer;
    }

    public void publishBeforeCommit(List<AuditTrail> auditTrails) {
        List<AuditString> auditStrings = auditTrails.stream()
                .map(auditTrail -> auditTrail.toAuditString(configurer))
                .collect(Collectors.toList());

        eventListener.beforeCommit(auditStrings);
    }
}