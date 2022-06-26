package com.lims.api.audit.context;

import com.lims.api.audit.config.AuditConfigurer;
import com.lims.api.audit.domain.RecordScope;

public class AuditManagerFactory {
    private final AuditConfigurer configurer;

    public AuditManagerFactory(AuditConfigurer configurer) {
        this.configurer = configurer;
    }

    public AuditManager create() {
        RecordScope scope = configurer.recordScope();

        if (scope.isEach()) {
            return new FullRecordAuditManager();
        }
        else if (scope.isTransaction()) {
            return new DecisiveRecordAuditManager();
        }

        throw new RuntimeException("Unknown auditTrail record scope. Configure record scope using AuditTrailConfigurer");
    }
}
