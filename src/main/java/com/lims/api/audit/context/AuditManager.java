package com.lims.api.audit.context;

import com.lims.api.audit.domain.AuditTrail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuditManager {

    private final ConcurrentHashMap<String, List<AuditTrail>> audit = new ConcurrentHashMap<>();

    public void put(String key, AuditTrail auditTrail) {
        if (has(key)) {
            audit.get(key).add(auditTrail);
        } else {
            audit.put(key, createList(auditTrail));
        }
    }

    public List<AuditTrail> get(String key) {
        return has(key) ? audit.get(key) : null;
    }

    public void remove(String key) {
        if (has(key)) {
            audit.remove(key);
        }
    }

    public boolean has(String key) {
        return audit.containsKey(key);
    }

    private List<AuditTrail> createList(AuditTrail auditTrail) {
        List<AuditTrail> list = new ArrayList<>();
        list.add(auditTrail);
        return list;
    }

}