package com.lims.api.common.dto;

import lombok.Getter;

@Getter
public class ValidationResult {
    private boolean verified;
    private String messageCode;

    public ValidationResult(boolean verified) {
        this.verified = verified;
    }

    public ValidationResult(boolean verified, String messageCode) {
        this.verified = verified;
        this.messageCode = messageCode;
    }
}