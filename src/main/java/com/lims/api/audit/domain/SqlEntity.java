package com.lims.api.audit.domain;

import java.lang.reflect.Field;
import java.util.List;

public class SqlEntity {
    private String name;
    private Class<?> target;
    private List<Field> idFields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public List<Field> getIdFields() {
        return idFields;
    }

    public void setIdFields(List<Field> idFields) {
        this.idFields = idFields;
    }
}