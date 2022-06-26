package com.lims.api.audit.context;

import com.lims.api.audit.domain.AuditAttribute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DecisiveRecordAuditManager extends AbstractAuditManager {
    private static final ThreadLocal<LinkedHashMap<String, AuditAttribute>> attributeStore = new ThreadLocal<>();

    public void put(String key, AuditAttribute auditAttribute) {
        if (attributeStore.get() == null) {
            attributeStore.set(new LinkedHashMap<>());
        }

        Map<String, AuditAttribute> resource = attributeStore.get();
        resource.put(key, auditAttribute);
    }

    public AuditAttribute get(String key) {
        if (!has(key)) {
            return null;
        }
        return attributeStore.get().get(key);
    }

    public boolean has(String key) {
        Map<String, AuditAttribute> resource = attributeStore.get();
        if (resource == null) {
            return false;
        }
        return attributeStore.get().containsKey(key);
    }

    public List<AuditAttribute> getAsList() {
        return new ArrayList<>(attributeStore.get().values());
    }

    public void remove() {
        System.out.println(attributeStore.get());
        attributeStore.remove();
    }

}