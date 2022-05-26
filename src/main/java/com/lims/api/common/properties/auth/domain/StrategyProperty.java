package com.lims.api.common.properties.auth.domain;

public enum StrategyProperty {
    COOKIE,
    HEADER;

    public boolean isCookie() {
        return this.name() == StrategyProperty.COOKIE.name();
    }

    public boolean isHeader() {
        return this.name() == StrategyProperty.HEADER.name();
    }

    public boolean equals(String value) {
        return this.name().equals(value.toUpperCase());
    }
}