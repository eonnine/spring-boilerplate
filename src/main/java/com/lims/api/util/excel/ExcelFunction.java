package com.lims.api.util.excel;

@FunctionalInterface
public interface ExcelFunction<T, S, R> {

    public R apply(T t, S u);

}