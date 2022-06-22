package com.lims.api.audit.service;

import com.lims.api.audit.domain.ColumnNameConverter;

public interface StringCaseConverter {

    public String convert(ColumnNameConverter stringCase, String str);

}