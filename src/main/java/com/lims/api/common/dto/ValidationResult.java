package com.lims.api.common.dto;

import lombok.Getter;

@Getter
public class ValidationResult {
    private boolean result;
    private String message;

    public ValidationResult(boolean verified) {
        this.result = verified;
    }

    public ValidationResult(boolean verified, String message) {
        this.result = verified;
        this.message = message;
    }
}