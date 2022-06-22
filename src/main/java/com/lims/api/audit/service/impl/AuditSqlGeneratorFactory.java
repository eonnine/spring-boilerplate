package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.DataBaseType;
import com.lims.api.audit.service.AuditSqlGenerator;

public class AuditSqlGeneratorFactory {

    public static AuditSqlGenerator create(DataBaseType dataBaseType) {
        if (dataBaseType.isOracle()) {
            return new OracleAuditSqlGenerator();
        }
        return new OracleAuditSqlGenerator();
    }

}