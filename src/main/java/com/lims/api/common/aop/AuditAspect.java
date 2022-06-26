package com.lims.api.common.aop;

import com.lims.api.audit.aop.AuditAdvisor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditAspect extends AuditAdvisor {
}
