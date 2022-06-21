package com.lims.api.audit.domain;

public class SqlParameter {
    private String name;
    private Object value;

    public SqlParameter() {}

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}