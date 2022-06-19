package com.lims.api.audit.domain;

import java.lang.reflect.Method;
import java.sql.SQLException;

public interface AuditTrail {

    void record(Method method, Object[] args, Object executeResult) throws SQLException, NoSuchFieldException;

}