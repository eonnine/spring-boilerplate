package com.lims.api.audit.transaction;

import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.domain.AuditTrail;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.List;

@Component
public class AuditTransactionListener implements TransactionSynchronization {

    private final AuditManager container;
    private final AuditEventPublisher eventPublisher;

    public AuditTransactionListener(AuditManager container, AuditEventPublisher eventPublisher) {
        this.container = container;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        if (!AuditTransactionManager.isCurrentTransactionActive()) {
            return;
        }
        eventPublisher.publishBeforeCommit(getAuditList());
    }

    @Override
    public void afterCommit() {
        if (!AuditTransactionManager.isCurrentTransactionActive()) {
            return;
        }
        eventPublisher.publishAfterCommit(getAuditList());
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

    private List<AuditTrail> getAuditList() {
        return container.get(AuditTransactionManager.getCurrentTransactionId());
    }

}