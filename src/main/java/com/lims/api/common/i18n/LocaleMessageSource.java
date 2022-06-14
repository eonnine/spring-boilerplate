package com.lims.api.common.i18n;

public interface LocaleMessageSource {

    public String getMessage(String messageCode);

    public String getMessage(String messageCode, Object[] args);

}