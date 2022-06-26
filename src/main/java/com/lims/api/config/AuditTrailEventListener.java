package com.lims.api.config;

import com.lims.api.audit.domain.AuditTrail;
import com.lims.api.audit.event.AuditEventListener;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AuditTrailEventListener implements AuditEventListener {

    @Override
    public void beforeCommit(List<AuditTrail> auditTrails) {
        auditTrails.forEach(auditTrail1 -> {
            System.out.println(auditTrail1.getCommandType());
            System.out.println(auditTrail1.getDiffString());
            System.out.println(auditTrail1.getLabel());
            System.out.println(auditTrail1.getContent());
        });
    }

}