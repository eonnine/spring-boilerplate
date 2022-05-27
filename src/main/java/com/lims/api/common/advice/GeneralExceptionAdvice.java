package com.lims.api.common.advice;

import com.lims.api.common.exception.UnAuthenticatedAccessException;
import com.lims.api.common.exception.UnAuthenticatedException;
import com.lims.api.common.util.i18n.service.LocaleMessageSource;
import com.lims.api.common.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GeneralExceptionAdvice {

    private final LocaleMessageSource messageSource;

    public GeneralExceptionAdvice(LocaleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> unAuthenticatedExceptionHandler(UnAuthenticatedException e) {
        return new ResponseEntity<>(new ErrorResponse(messageSource.getMessage(e.getMessageCode())), e.getStatus());
    }

    @ExceptionHandler(UnAuthenticatedAccessException.class)
    public ResponseEntity<ErrorResponse> unAuthenticatedAccessExceptionHandler(UnAuthenticatedAccessException e) {
        return new ResponseEntity<>(new ErrorResponse(messageSource.getMessage(e.getMessageCode())), e.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> httpClientErrorExceptionHandler(HttpClientErrorException e) {
        return new ResponseEntity<>(new ErrorResponse(messageSource.getMessage(e.getStatusText())), e.getStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return new ResponseEntity<>(new ErrorResponse(messageSource.getMessage("error,http.notAllowMethod")), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}