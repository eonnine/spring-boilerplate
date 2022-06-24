package com.lims.api.audit.transaction;

import com.lims.api.audit.context.AuditContainer;
import com.lims.api.audit.domain.AuditTrail;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

public class AuditTransactionListener implements TransactionSynchronization {

    private final AuditContainer container;
    private final AuditTrailEventPublisher eventPublisher;

    public AuditTransactionListener(AuditContainer container, AuditTrailEventPublisher eventPublisher) {
        this.container = container;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        System.out.println(TransactionSynchronizationManager.getSynchronizations());
        if (!AuditTransactionManager.isCurrentTransactionActive()) {
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