package com.lims.api.audit.domain;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AuditTrailTransactionEventListener {


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(Boolean readonly) {
        System.out.println(readonly);
        System.out.println("@@@@@@@@@@@@@@@@@");
    }

}