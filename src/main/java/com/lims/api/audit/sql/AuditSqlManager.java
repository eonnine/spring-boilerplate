package com.lims.api.audit.sql;

import com.lims.api.audit.domain.SqlEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuditSqlManager {

    private final Map<String, SqlEntity> sql = new HashMap<>();

    public void put(String name, SqlEntity entity) {
        sql.put(name, entity);
    }

    public SqlEntity get(String name) {
        return sql.get(name);
    }

    public Map<String, SqlEntity> getAll() {
        return sql;
    }
}