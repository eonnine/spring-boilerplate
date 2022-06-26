package com.lims.api.audit.domain;

public enum CommandType {
    INSERT,
    UPDATE,
    DELETE;

    public boolean isInsert() {
        return this == CommandType.INSERT;
    }
}
