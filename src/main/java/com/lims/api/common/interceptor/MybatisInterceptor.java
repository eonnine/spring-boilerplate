package com.lims.api.common.interceptor;

import com.lims.api.common.annotation.Audit;
import com.lims.api.common.service.AuditTrailService;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.Statement;

@Intercepts({
    @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
})
public class MybatisInterceptor implements Interceptor {

    private AuditTrailService auditTrailService;

    public MybatisInterceptor(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object queryResult = invocation.proceed();

        if (auditTrailService.hasAnnotation(invocation.getMethod()) && isAuditTrailTarget(queryResult)) {
            auditTrailService.recordAuditTrail(invocation);
        }

        return queryResult;
    }

    private boolean isAuditTrailTarget(Object queryResult) {
        return ((Integer) queryResult) > 0;
    }

}