package com.lims.api.audit.transaction;

import com.lims.api.audit.domain.AuditString;

import java.util.List;

public interface AuditTrailEventListener {

    default void beforeCommit(List<AuditString> auditTrail) {};

    default void afterCompletion(List<AuditString> auditTrail) {};

}