package com.lims.api.auth.domain;

public class Token {

    private final String value;

    public Token(String token) {
        this.value = token;
    }

    public String get() {
        return value;
    }

}