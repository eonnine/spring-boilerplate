package com.lims.api.audit.domain;

import java.lang.reflect.Method;
import java.util.List;

public class AuditTrail {
    private Method method;
    private boolean updated;
    private AuditType type;
    private String label;
    private String content;
    private List<SqlRow> originRows;
    private List<SqlRow> updatedRows;

    public AuditTrail(Method method, AuditType type, String label, String content, List<SqlRow> origin) {
        this.type = type;
        this.label = label;
        this.content = content;
        this.originRows = origin;
    }

    public boolean isUpdated() { return this.updated; }

    public String getDiffToString() {
        return null;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public void setUpdatedRows(List<SqlRow> updated) {
        this.updatedRows = updated;
    }

    public static AuditTrail.AuditTrailBuilder builder() {
        return new AuditTrail.AuditTrailBuilder();
    }

    public static class AuditTrailBuilder {
        private Method method;
        private AuditType type;
        private String label;
        private String content;
        private List<SqlRow> originRows;

        AuditTrailBuilder() {
        }

        public AuditTrail.AuditTrailBuilder type(Method method) {
            this.method = method;
            return this;
        }

        public AuditTrail.AuditTrailBuilder type(AuditType type) {
            this.type = type;
            return this;
        }

        public AuditTrail.AuditTrailBuilder label(Object value) {
            this.label = label;
            return this;
        }

        public AuditTrail.AuditTrailBuilder content(String content) {
            this.content = content;
            return this;
        }

        public AuditTrail.AuditTrailBuilder origin(List<SqlRow> origin) {
            this.originRows = origin;
            return this;
        }

        public AuditTrail build() {
            return new AuditTrail(this.method, this.type, this.label, this.content, this.originRows);
        }
    }

}