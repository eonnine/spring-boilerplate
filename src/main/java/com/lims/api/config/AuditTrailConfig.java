package com.lims.api.config;

import com.lims.api.audit.config.AuditConfigurer;
import com.lims.api.audit.domain.RecordScope;
import com.lims.api.audit.domain.DataBaseType;
import com.lims.api.audit.domain.DisplayType;
import com.lims.api.audit.domain.StringConvertCase;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditTrailConfig implements AuditConfigurer {

    @Override
    public StringConvertCase convertCase() {
        return AuditConfigurer.super.convertCase();
    }

    @Override
    public DataBaseType databaseType() {
        return AuditConfigurer.super.databaseType();
    }
}