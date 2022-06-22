package com.lims.api.audit.domain;

public enum DataBaseType {
    ORACLE;

    public boolean isOracle() {
        return this == DataBaseType.ORACLE;
    }
}