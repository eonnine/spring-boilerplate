package com.lims.api.audit.context;

import java.lang.reflect.Method;

public interface AuditTrailContext {

    void preSnapshot(Method method, Object[] args);

    void postSnapshot(Method method, Object[] args, Object result);

}