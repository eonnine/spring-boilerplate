package com.lims.api.config.properties.auth.domain;

public enum AuthStrategyProperty {
    TOKEN;

    public boolean isToken() {
        return this == AuthStrategyProperty.TOKEN;
    }

    public boolean equals(String value) {
        return value != null && this.name().equals(value.toUpperCase());
    }
}
