package com.lims.api.audit.sql;

import com.lims.api.audit.config.AuditTrailConfigurer;
import com.lims.api.audit.domain.DataBaseType;
import org.springframework.stereotype.Component;

@Component
public class AuditSqlGeneratorFactory {

    private final AuditTrailConfigurer configurer;

    public AuditSqlGeneratorFactory(AuditTrailConfigurer configurer) {
        this.configurer = configurer;
    }

    public AuditSqlGenerator create() {
        DataBaseType type = configurer.databaseType();

        if (type.isOracle()) {
            return new OracleAuditSqlGenerator();
        }

        throw new RuntimeException("Unknown auditTrail database type. Configure database type using AuditTrailConfigurer");
    }

}