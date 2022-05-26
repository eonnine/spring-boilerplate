package com.lims.api.common.properties.auth.domain;

public enum TokenTypeProperty {
    COOKIE(""),
    BEARER("Bearer");

    private String prefix;

    TokenTypeProperty(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}