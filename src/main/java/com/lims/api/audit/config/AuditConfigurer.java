package com.lims.api.audit.config;

import com.lims.api.audit.domain.DataBaseType;
import com.lims.api.audit.domain.DisplayType;
import com.lims.api.audit.domain.StringConvertCase;

public interface AuditConfigurer {

    default DisplayType displayType() {
        return DisplayType.COMMENT;
    };

    default StringConvertCase convertCase() {
        return StringConvertCase.CAMEL_TO_SNAKE;
    };

    default DataBaseType databaseType() {
        return DataBaseType.ORACLE;
    }

}