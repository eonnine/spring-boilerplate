package com.lims.api.audit.domain;

import java.lang.reflect.Field;
import java.util.List;

public class SqlEntity {
    private final String name;
    private final Class<?> target;
    private final List<Field> idFields;

    public SqlEntity(String name, Class<?> target, List<Field> idFields) {
        this.name = name;
        this.target = target;
        this.idFields = idFields;
    }

    public String getName() {
        return name;
    }

    public Class<?> getTarget() {
        return target;
    }

    public List<Field> getIdFields() {
        return idFields;
    }
}
