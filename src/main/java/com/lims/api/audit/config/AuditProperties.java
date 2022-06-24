package com.lims.api.audit.config;

import com.lims.api.audit.domain.DataBaseType;
import com.lims.api.audit.domain.DisplayType;
import com.lims.api.audit.domain.StringConvertCase;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {
    private final DisplayType display;
    private final StringConvertCase convertCase;
    private final DataBaseType database;

    public AuditProperties(DisplayType display, StringConvertCase convertCase, DataBaseType database) {
        this.display = display;
        this.convertCase = convertCase;
        this.database = database;
    }
}