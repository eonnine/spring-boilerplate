package com.lims.api.common.properties.auth.domain;

public abstract class TokenProperty {
    private final ExpireProperty expire;

    public TokenProperty(ExpireProperty expire) {
        this.expire = expire == null ? getDefaultExpire() : expire;
    }

    public ExpireProperty getExpire() {
        return expire;
    }

    public abstract String getName();

    protected abstract ExpireProperty getDefaultExpire();
}