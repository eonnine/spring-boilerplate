package com.lims.api.audit.transaction;

import com.lims.api.audit.config.AuditConfigurer;
import com.lims.api.audit.context.AuditManager;
import com.lims.api.audit.domain.AuditAttribute;
import com.lims.api.audit.domain.AuditTrail;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuditTransactionListener implements TransactionSynchronization {

    private final AuditConfigurer configurer;
    private final AuditManager auditManager;
    private final AuditEventPublisher eventPublisher;

    public AuditTransactionListener(AuditConfigurer configurer, AuditManager auditManager, AuditEventPublisher eventPublisher) {
        this.configurer = configurer;
        this.auditManager = auditManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        if (!AuditTransactionManager.isCurrentTransactionActive()) {
            return;
        }
        List<AuditTrail> audits = convertToAudit(auditManager.getAsList());
        auditManager.putAudits(audits);
        eventPublisher.publishBeforeCommit(audits);
        throw new RuntimeException("rollback!");
    }

    @Override
    public void afterCommit() {
        if (!AuditTransactionManager.isCurrentTransactionActive()) {
            return;
        }
        eventPublisher.publishAfterCommit(auditManager.getAudits());
    }

    @Override
    public void afterCompletion(int status) {
        clear();
    }

    private void clear() {
        auditManager.remove();
        AuditTransactionManager.removeCurrentTransactionId();
    }

    private List<AuditTrail> convertToAudit(List<AuditAttribute> attributes) {
        return attributes.stream()
                .filter(AuditAttribute::isUpdated)
                .map(attribute -> attribute.toAuditTail(configurer))
                .collect(Collectors.toList());
    }

}