package com.lims.api.audit.domain;

import java.util.Arrays;

public enum SqlType {
    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private String value;

    SqlType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static SqlType getByValue(String value) {
        return Arrays.stream(SqlType.values()).filter(sqlType -> sqlType.getValue().equals(value)).findAny().orElse(null);
    }
}