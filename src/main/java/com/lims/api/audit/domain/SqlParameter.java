package com.lims.api.audit.domain;

public class SqlParameter {
    private String name;
    private Object data;

    public SqlParameter() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}