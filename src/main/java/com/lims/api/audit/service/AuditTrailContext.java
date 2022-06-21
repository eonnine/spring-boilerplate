package com.lims.api.audit.service;

import java.lang.reflect.Method;
import java.sql.SQLException;

public interface AuditTrailContext {

    void preSnapshot(Method method, Object[] args) throws SQLException, NoSuchFieldException;

    void postSnapshot(Method method, Object[] args, Object result) throws SQLException, NoSuchFieldException;

}
