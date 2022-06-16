package com.lims.api.common.service;

import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Method;
import java.sql.SQLException;

public interface AuditTrailService {

    void recordAuditTrail(Invocation invocation) throws SQLException;

    boolean hasAnnotation(Method method);

}