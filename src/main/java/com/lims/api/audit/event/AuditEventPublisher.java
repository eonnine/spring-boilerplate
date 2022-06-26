package com.lims.api.audit.event;

import com.lims.api.audit.domain.AuditTrail;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

public class AuditEventPublisher {

    private final AuditEventListener eventListener;

    public AuditEventPublisher(AuditEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void publishBeforeCommit(List<AuditTrail> auditTrails) {
        eventListener.beforeCommit(auditTrails);
    }

    public void publishAfterCommit(List<AuditTrail> auditTrails) {
        eventListener.afterCommit(auditTrails);
    }
}