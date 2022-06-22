package com.lims.api.audit.domain;

public class AuditString {

    private final String label;
    private final String content;
    private final String diffString;

    public String getLabel() {
        return this.label;
    }

    public String getContent() {
        return this.content;
    }

    public String getDiffString() {
        return this.diffString;
    }

    AuditString(final String label, final String content, final String diffString) {
        this.label = label;
        this.content = content;
        this.diffString = diffString;
    }

    public static AuditString.AuditStringBuilder builder() {
        return new AuditString.AuditStringBuilder();
    }

    public static class AuditStringBuilder {
        private String label;
        private String content;
        private String diffString;

        AuditStringBuilder() {
        }

        public AuditString.AuditStringBuilder label(String label) {
            this.label = label;
            return this;
        }

        public AuditString.AuditStringBuilder content(String content) {
            this.content = content;
            return this;
        }

        public AuditString.AuditStringBuilder diffString(String diffString) {
            this.diffString = diffString;
            return this;
        }

        public AuditString build() {
            return new AuditString(this.label, this.content, this.diffString);
        }
    }
}