package com.lims.api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String messageCode;

    public BaseException(HttpStatus status, String messageCode) {
        this.status = status;
        this.messageCode = messageCode;
    }
}