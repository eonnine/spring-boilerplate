package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.AuditTrail;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

public class AuditTrailRecorder implements TransactionSynchronization {

    private final AuditTrailContainer container;

    public AuditTrailRecorder(AuditTrailContainer container) {
        this.container = container;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        List<AuditTrail> list =  container.get(AuditTrailTransaction.getCurrentTransactionId());
        System.out.println("----------Record Audit Trail!!!");
        System.out.println(list.size());
        System.out.println(TransactionSynchronizationManager.getResourceMap().entrySet().toString());

        list.stream().forEach(auditTrail -> {
            System.out.println(auditTrail.isUpdated());
        });

        // TODO (grouping 설정에 따라 병합해서 insert or 개별 insert
        // execute query
    }
}