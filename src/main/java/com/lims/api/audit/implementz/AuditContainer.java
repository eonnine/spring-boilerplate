package com.lims.api.audit.implementz;

import com.lims.api.audit.domain.AuditTrail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuditContainer {

    private final ConcurrentHashMap<String, List<AuditTrail>> store = new ConcurrentHashMap<>();

    public void put(String key, AuditTrail auditTrail) {
        if (has(key)) {
            store.get(key).add(auditTrail);
        } else {
            store.put(key, createList(auditTrail));
        }
    }

    public List<AuditTrail> get(String key) {
        return has(key) ? store.get(key) : null;
    }

    public void remove(String key) {
        if (has(key)) {
            store.remove(key);
        }
    }

    public boolean has(String key) {
        return store.containsKey(key);
    }

    private List<AuditTrail> createList(AuditTrail auditTrail) {
        List<AuditTrail> list = new ArrayList<>();
        list.add(auditTrail);
        return list;
    }

}