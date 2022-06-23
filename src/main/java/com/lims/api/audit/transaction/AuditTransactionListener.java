package com.lims.api.audit.transaction;

import com.lims.api.audit.domain.AuditTrail;
import com.lims.api.audit.implementz.AuditContainer;
import com.lims.api.audit.event.AuditEventPublisher;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.List;

public class AuditTransactionListener implements TransactionSynchronization {

    private final AuditContainer container;
    private final AuditEventPublisher eventPublisher;

    public AuditTransactionListener(AuditContainer container, AuditEventPublisher eventPublisher) {
        this.container = container;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        if (!AuditTransactionManager.hasResourceCurrentTransaction()) {
            return;
        }

        List<AuditTrail> auditTrails =  container.get(AuditTransactionManager.getCurrentTransactionId());
        eventPublisher.publishBeforeCommit(auditTrails);
    }

    @Override
    public void afterCompletion(int status) {
        clear();
    }

    private void clear() {
        String transactionId = AuditTransactionManager.getCurrentTransactionId();
        container.remove(transactionId);
        AuditTransactionManager.removeCurrentTransactionId();
    }

}