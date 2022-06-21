package com.lims.api.audit.annotation;

import com.lims.api.audit.domain.AuditType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {

    Class<?> target();

    String label() default "";

    AuditType type() default AuditType.DATA;

    String content() default "";

}