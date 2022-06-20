package com.lims.api.audit.service.impl;

import org.springframework.transaction.support.TransactionSynchronization;

public class AuditTrailRecorder implements TransactionSynchronization {

    private final AuditTrailContainer container;

    public AuditTrailRecorder(AuditTrailContainer container) {
        this.container = container;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        System.out.println("Record Audit Trail!!!");
    }
}
