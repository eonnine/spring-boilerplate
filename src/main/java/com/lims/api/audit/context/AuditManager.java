package com.lims.api.audit.context;

import com.lims.api.audit.domain.AuditTrail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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

    public AuditTrail getValueLast(String key) {
        List<AuditTrail> auditTrails = audit.get(key);
        if (auditTrails.size() == 0) {
            return null;
        }
        return auditTrails.get(auditTrails.size() - 1);
    }

    public void remove(String key) {
        if (has(key)) {
            audit.remove(key);
        }
    }

    public void removeValueLast(String key) {
        List<AuditTrail> auditTrails = audit.get(key);
        if (auditTrails.size() == 0) {
            return;
        }
        auditTrails.remove(auditTrails.size() - 1);
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