package com.lims.api.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class BaseException extends Exception {
    private final HttpStatus status;
    private final String messageCode;
}