package com.lims.api.audit.config;

import com.lims.api.audit.domain.DataBaseType;
import com.lims.api.audit.domain.DisplayType;
import com.lims.api.audit.domain.RecordScope;
import com.lims.api.audit.domain.StringConvertCase;

public class DefaultAuditConfigurer implements AuditConfigurer {

    private final AuditProperties auditProperties;

    public DefaultAuditConfigurer(AuditProperties auditProperties) {
        this.auditProperties = auditProperties;
    }

    @Override
    public RecordScope recordScope() {
        if (auditProperties.getRecordScope() != null) {
            return auditProperties.getRecordScope();
        }
        return AuditConfigurer.super.recordScope();
    }

    @Override
    public DisplayType displayType() {
        if (auditProperties.getDisplay() != null) {
            return auditProperties.getDisplay();
        }
        return AuditConfigurer.super.displayType();
    }

    @Override
    public StringConvertCase convertCase() {
        if (auditProperties.getConvertCase() != null) {
            return auditProperties.getConvertCase();
        }
        return AuditConfigurer.super.convertCase();
    }

    @Override
    public DataBaseType databaseType() {
        if (auditProperties.getDatabase() != null) {
            return auditProperties.getDatabase();
        }
        return AuditConfigurer.super.databaseType();
    }
}