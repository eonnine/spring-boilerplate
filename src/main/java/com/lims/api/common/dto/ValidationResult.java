package com.lims.api.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidationResult {
    private boolean verified;
    private String messageCode;
}