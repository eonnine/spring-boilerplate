package com.lims.api.audit.service.impl;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

public class AuditTransactionManager {

    private static final String RESOURCE_NAME = "auditTailIdentifier";

    private static String createTransactionId() {
        return UUID.randomUUID().toString();
    }

    protected static String initResourceCurrentTransaction() {
        if (hasResourceCurrentTransaction()) {
            throw new RuntimeException("Already exists auditTrail resource in current transaction [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
        }
        String transactionId = createTransactionId();
        TransactionSynchronizationManager.bindResource(RESOURCE_NAME, transactionId);
        return transactionId;
    }

    protected static String getCurrentTransactionId() {
        if (!hasResourceCurrentTransaction()) {
            throw new RuntimeException("Not found current auditTrail resource in current transaction [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
        }
        return (String) TransactionSynchronizationManager.getResource(RESOURCE_NAME);
    }

    protected static void removeCurrentTransactionId() {
        if (hasResourceCurrentTransaction()) {
            TransactionSynchronizationManager.unbindResource(RESOURCE_NAME);
        }
    }

    protected static boolean hasResourceCurrentTransaction() {
        return TransactionSynchronizationManager.hasResource(RESOURCE_NAME);
    }

}