package com.lims.api.common.util.i18n.service;

public interface LocaleMessageSource {

    public String getMessage(String messageCode);

    public String getMessage(String messageCode, Object[] args);

}