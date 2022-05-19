package com.lims.api.common.i18n.service;

public interface LocaleMessageSource {

    public String getMessage(String messageCode);

    public String getMessage(String messageCode, Object[] args);

}