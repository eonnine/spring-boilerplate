package com.lims.api.audit.domain;

public class SqlParameter {
    private String name;
    private Object value;

    SqlParameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public static SqlParameter.SqlParameterBuilder builder() {
        return new SqlParameter.SqlParameterBuilder();
    }

    public static class SqlParameterBuilder {
        private String name;
        private Object value;

        SqlParameterBuilder() {
        }

        public SqlParameter.SqlParameterBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SqlParameter.SqlParameterBuilder value(Object value) {
            this.value = value;
            return this;
        }

        public SqlParameter build() {
            return new SqlParameter(this.name, this.value);
        }
    }

}