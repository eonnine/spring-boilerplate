package com.lims.api.common.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidationResult {
    private boolean verified;
    private String messageCode;
}