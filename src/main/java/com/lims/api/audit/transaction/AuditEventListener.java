package com.lims.api.audit.transaction;

import com.lims.api.audit.domain.AuditString;

import java.util.List;

public interface AuditEventListener {

    default void beforeCommit(List<AuditString> auditTrail) {};

    default void afterCommit(List<AuditString> auditTrail) {};

}