package com.lims.api.auth.domain;

import com.lims.api.config.properties.auth.domain.TokenStrategyProperty;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CookieStrategyCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String strategy = context.getEnvironment().getProperty("auth.token.strategy");
        return strategy == null || TokenStrategyProperty.COOKIE.equals(strategy);
    }
}
