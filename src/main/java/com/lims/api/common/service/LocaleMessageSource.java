package com.lims.api.common.service;

public interface LocaleMessageSource {

    public String getMessage(String messageCode);

    public String getMessage(String messageCode, Object[] args);

}