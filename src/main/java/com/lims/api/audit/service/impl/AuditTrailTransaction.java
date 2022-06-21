package com.lims.api.audit.service.impl;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

public class AuditTrailTransaction {

    private static final String RESOURCE_NAME = "auditTailIdentifier";

    public static String initResourceCurrentTransaction() {
        if (hasResourceCurrentTransaction()) {
            throw new RuntimeException("Already exists auditTrail resource in current transaction [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
        }
        String transactionId = createTransactionId();
        TransactionSynchronizationManager.bindResource(RESOURCE_NAME, transactionId);
        return transactionId;
    }

    public static String getCurrentTransactionId() {
        if (!hasResourceCurrentTransaction()) {
            throw new RuntimeException("Not found current auditTrail resource in current transaction [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
        }
        return (String) TransactionSynchronizationManager.getResource(RESOURCE_NAME);
    }

    public static boolean hasResourceCurrentTransaction() {
        return TransactionSynchronizationManager.hasResource(RESOURCE_NAME);
    }

    public static String createTransactionId() {
        return UUID.randomUUID().toString();
    }

}