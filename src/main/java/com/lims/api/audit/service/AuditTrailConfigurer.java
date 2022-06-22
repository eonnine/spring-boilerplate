package com.lims.api.audit.service;

import com.lims.api.audit.domain.DisplayType;
import com.lims.api.audit.domain.StringConvertCase;

public interface AuditTrailConfigurer {

    default DisplayType displayType() {
        return DisplayType.COMMENT;
    };

    default StringConvertCase convertCase() {
        return StringConvertCase.CAMEL_TO_SNAKE;
    };

}