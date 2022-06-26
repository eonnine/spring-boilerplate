package com.lims.api.audit.context;

import com.lims.api.audit.domain.AuditAttribute;
import com.lims.api.audit.domain.AuditTrail;

import java.util.List;

public interface AuditManager {

    void put(String key, AuditAttribute auditTrail);

    AuditAttribute get(String key);

    boolean has(String key);

    void remove();

    List<AuditAttribute> getAsList();

    void putAudits(List<AuditTrail> audits);

    List<AuditTrail> getAudits();

}
