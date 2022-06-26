package com.lims.api.audit.event;

import com.lims.api.audit.domain.AuditTrail;

import java.util.List;

public interface AuditEventListener {

    default void beforeCommit(List<AuditTrail> auditTrail) {};

    default void afterCommit(List<AuditTrail> auditTrail) {};

}