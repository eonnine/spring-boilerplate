package com.lims.api.audit.service;

import com.lims.api.audit.domain.StringCase;

public interface StringCaseConverter {

    public String convert(StringCase stringCase, String str);

}
