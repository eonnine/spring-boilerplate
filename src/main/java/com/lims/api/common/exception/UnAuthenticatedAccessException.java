package com.lims.api.common.exception;

import org.springframework.http.HttpStatus;

public class UnAuthenticatedAccessException extends BaseException {

    public UnAuthenticatedAccessException() {
        super(HttpStatus.FORBIDDEN, "error.auth.unauthorizedAccess");
    }

    public UnAuthenticatedAccessException(HttpStatus status) {
        super(status, "error.auth.unauthorizedAccess");
    }

    public UnAuthenticatedAccessException(String messageCode) {
        super(HttpStatus.FORBIDDEN, messageCode);
    }

    public UnAuthenticatedAccessException(HttpStatus status, String messageCode) {
        super(status, messageCode);
    }
}