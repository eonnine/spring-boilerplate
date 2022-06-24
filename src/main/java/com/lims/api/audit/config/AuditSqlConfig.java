package com.lims.api.audit.config;

import com.lims.api.audit.annotation.AuditEntity;
import com.lims.api.audit.domain.SqlEntity;
import com.lims.api.audit.sql.AuditSqlManager;
import com.lims.api.audit.sql.AuditSqlProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;
import java.util.Set;

@Configuration
public class AuditSqlConfig {

    private final AuditSqlManager sqlManager;
    private final AuditSqlProvider sqlProvider;

    public AuditSqlConfig(AuditSqlManager sqlManager, AuditSqlProvider sqlProvider) {
        this.sqlManager = sqlManager;
        this.sqlProvider = sqlProvider;
        initialize();
    }

    public void initialize() {
        try {
            Set<BeanDefinition> entities = getEntities();

            for (BeanDefinition bd : entities) {
                String className = bd.getBeanClassName();
                Class<?> entityClazz = Class.forName(className);
                AuditEntity entityAnnotation = entityClazz.getAnnotation(AuditEntity.class);
                List<String> idFields = sqlProvider.getIdFields(entityClazz);

                String selectClause = sqlProvider.generateSelectClause(entityClazz, entityAnnotation.name());

                SqlEntity entity = new SqlEntity();
                entity.setName(className);
                entity.setTarget(entityClazz);
                entity.setIdFields(idFields);
                entity.setSelectClause(selectClause);

                sqlManager.put(className, entity);
            }
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("Not found AuditEntity. " + e.getMessage());
        }
        System.out.println(sqlManager.getAll());
    }

    private Set<BeanDefinition> getEntities() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(AuditEntity.class));
        return scanner.findCandidateComponents("**/*");
    }

}