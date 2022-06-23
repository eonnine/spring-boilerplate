package com.lims.api.config;

import com.lims.api.audit.domain.AuditString;
import com.lims.api.audit.event.AuditEventListener;

import java.util.List;

public class AuditTrailEventListener implements AuditEventListener {

    @Override
    public void pointCut() {

    }

    @Override
    public void beforeCommit(List<AuditString> auditTrail) {
        auditTrail.forEach(auditString -> {
            System.out.println(auditString.getDiffString());
            System.out.println(auditString.getLabel());
            System.out.println(auditString.getContent());
        });
    }
}