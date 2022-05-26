package com.lims.api.common.dto;

import lombok.Getter;

@Getter
public class ValidationResult {
    private boolean verified;
    private String message;

    public ValidationResult(boolean verified) {
        this.verified = verified;
    }

    public ValidationResult(boolean verified, String message) {
        this.verified = verified;
        this.message = message;
    }
}