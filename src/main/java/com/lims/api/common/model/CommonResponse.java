package com.lims.api.common.model;

import lombok.Getter;

@Getter
public class CommonResponse {

    private final boolean result;
    private final String message;

    public CommonResponse(boolean result) {
        this.result = result;
        this.message = null;
    }

    public CommonResponse(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
