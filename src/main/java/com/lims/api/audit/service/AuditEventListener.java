package com.lims.api.audit.service;

import com.lims.api.audit.domain.AuditString;

import java.util.List;

public interface AuditEventListener {

    default void beforeCommit(List<AuditString> auditTrail) {};

}