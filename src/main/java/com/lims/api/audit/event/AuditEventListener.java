package com.lims.api.audit.event;

import com.lims.api.audit.domain.AuditString;

import java.util.List;

public interface AuditEventListener {

    void pointCut();

    default void beforeCommit(List<AuditString> auditTrail) {};

}