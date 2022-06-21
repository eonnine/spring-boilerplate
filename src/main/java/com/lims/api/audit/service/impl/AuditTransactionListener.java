package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.AuditTrail;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

public class AuditTransactionListener implements TransactionSynchronization {

    private final AuditContainer container;
    private final AuditRepository repository;

    public AuditTransactionListener(AuditContainer container, AuditRepository repository) {
        this.container = container;
        this.repository = repository;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        List<TransactionSynchronization> a =  TransactionSynchronizationManager.getSynchronizations();
        System.out.println("beforeCommit: " + a.size());
        executeAuditTrail();
    }

    @Override
    public void afterCompletion(int status) {
        clear();
    }

    private void executeAuditTrail() {
        if (!AuditTransactionManager.hasResourceCurrentTransaction()) {
            return;
        }
        List<AuditTrail> list =  container.get(AuditTransactionManager.getCurrentTransactionId());

        list.stream().forEach(auditTrail -> {
            System.out.println(auditTrail.toDiffString());
            System.out.println(auditTrail.toDiffString().length());
        });
        // TODO (grouping 설정에 따라 병합해서 insert or 개별 insert
        // execute query
    }

    private void clear() {
        String transactionId = AuditTransactionManager.getCurrentTransactionId();
        container.remove(transactionId);
        AuditTransactionManager.removeCurrentTransactionId();
    }

}
