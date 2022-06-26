package com.lims.api.audit.domain;

@FunctionalInterface
public interface DisplayTypeFunction<T, S, U> {

    T apply(T t, S s, U u);

}
