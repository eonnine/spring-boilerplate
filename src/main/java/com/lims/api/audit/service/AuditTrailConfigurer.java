package com.lims.api.audit.service;

import com.lims.api.audit.domain.AuditRecordStrategy;
import com.lims.api.audit.domain.ColumnNameConverter;
import com.lims.api.audit.domain.DisplayType;

public interface AuditTrailConfigurer {

    default DisplayType displayType() {
        return DisplayType.COMMENT;
    };

    default ColumnNameConverter converter() {
        return null;
    };

}