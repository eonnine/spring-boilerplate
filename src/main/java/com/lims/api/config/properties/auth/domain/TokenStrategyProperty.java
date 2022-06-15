package com.lims.api.config.properties.auth.domain;

public enum TokenStrategyProperty {
    COOKIE("Bearer_"),
    HEADER("Bearer ");

    private final String prefix;

    TokenStrategyProperty(String prefix) {
        this.prefix = prefix;
    }

    public boolean isCookie() {
        return this == TokenStrategyProperty.COOKIE;
    }

    public boolean isHeader() { return this == TokenStrategyProperty.HEADER; }

    public boolean equals(String value) {
        return value != null && this.name().equals(value.toUpperCase());
    }

    public String getPrefix() {
        return prefix;
    }

    public static TokenStrategyProperty getDefaultStrategy() {
        return TokenStrategyProperty.COOKIE;
    }
}