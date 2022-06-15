package com.lims.api.auth.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.context.annotation.Conditional;

public class DefaultOrCookieTokenStrategyCondition extends AnyNestedCondition {

    public DefaultOrCookieTokenStrategyCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @Conditional(DefaultTokenStrategyCondition.class)
    static class OnDefault {}

    @Conditional(CookieTokenStrategyCondition.class)
    static class OnCookie {}
}