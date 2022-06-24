package com.lims.api.config;

import com.lims.api.audit.domain.AuditString;

import java.util.List;

//@Configuration
public class AuditTrailEventListener implements com.lims.api.audit.transaction.AuditTrailEventListener {

    @Override
    public void beforeCommit(List<AuditString> auditTrail) {
        auditTrail.forEach(auditString -> {
            System.out.println(auditString.getDiffString());
            System.out.println(auditString.getLabel());
            System.out.println(auditString.getContent());
        });
    }
}