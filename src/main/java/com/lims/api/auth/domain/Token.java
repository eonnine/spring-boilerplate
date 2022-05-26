package com.lims.api.auth.domain;

import com.lims.api.auth.service.TokenProvider;
import com.lims.api.common.dto.ValidationResult;

public class Token {

    private final String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}