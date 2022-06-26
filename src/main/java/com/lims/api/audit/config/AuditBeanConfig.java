package com.lims.api.audit.config;

import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.context.DecisiveRecordAuditManager;
import com.lims.api.audit.context.FullRecordAuditManager;
import com.lims.api.audit.domain.DataBaseType;
import com.lims.api.audit.domain.RecordScope;
import com.lims.api.audit.event.AuditEventListener;
import com.lims.api.audit.event.AuditEventPublisher;
import com.lims.api.audit.event.AuditTransactionListener;
import com.lims.api.audit.event.DefaultAuditEventListener;
import com.lims.api.audit.sql.*;
import com.lims.api.audit.util.AuditAnnotationReader;
import com.lims.api.audit.util.FieldNameConverter;
import com.lims.api.audit.util.StringConverter;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class AuditBeanConfig {

    private Map<String, Object> bean = new HashMap<>();

    /**
     * context
     */
    public AuditConfigurer configurer() {
        ApplicationContext context = new AnnotationConfigApplicationContext();
        AuditConfigurer configurer =  context.getBean(AuditConfigurer.class);
        return configurer;
    }

    public DefaultAuditConfigurer defaultAuditConfigurer() {
        return new DefaultAuditConfigurer();
    }

    public AuditManager auditManager() {
        RecordScope scope = configurer().recordScope();
        if (scope.isEach()) {
            return fullRecordAuditManager();
        }
        else if (scope.isTransaction()) {
            return decisiveRecordAuditManager();
        }
        throw new RuntimeException("Unknown auditTrail record scope. Configure record scope using AuditTrailConfigurer");
    }

    public FullRecordAuditManager fullRecordAuditManager() {
        return new FullRecordAuditManager();
    }

    public DecisiveRecordAuditManager decisiveRecordAuditManager() {
        return new DecisiveRecordAuditManager();
    }

    /**
     * sql
     */
    public AuditSqlConfig auditSqlConfig() {
        return new AuditSqlConfig(auditSqlManager(), auditSqlProvider(), annotationReader());
    }

    public AuditSqlRepository auditSqlRepository() {
        auditSqlConfig().initialize();
        return new AuditSqlRepository(dataSource(), auditSqlManager(), auditSqlProvider());
    }

    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    public AuditSqlManager auditSqlManager() {
        return new AuditSqlManager();
    }

    public AuditSqlProvider auditSqlProvider() {
        return new AuditSqlProvider(auditSqlGenerator(), stringConverter());
    }

    public AuditSqlGenerator auditSqlGenerator() {
        DataBaseType dataBaseType = configurer().databaseType();
        if (dataBaseType.isOracle()) {
            return oracleAuditSqlGenerator();
        }
        return oracleAuditSqlGenerator();
    }

    public OracleAuditSqlGenerator oracleAuditSqlGenerator() {
        return new OracleAuditSqlGenerator();
    }

    /**
     * event
     */
    public AuditTransactionListener auditTransactionListener() {
        return new AuditTransactionListener(configurer(), auditManager(), auditEventPublisher());
    }

    public AuditEventPublisher auditEventPublisher() {
        return new AuditEventPublisher(auditEventListener());
    }

    public AuditEventListener auditEventListener() {
        return defaultAuditEventListener();
    }

    public DefaultAuditEventListener defaultAuditEventListener() {
        return new DefaultAuditEventListener();
    }

    /**
     * util
     */
    public AuditAnnotationReader annotationReader() {
        return new AuditAnnotationReader();
    }

    public StringConverter stringConverter() {
        return new FieldNameConverter(configurer());
    }

}
