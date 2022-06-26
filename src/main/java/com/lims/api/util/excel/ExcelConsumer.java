package com.lims.api.util.excel;

@FunctionalInterface
public interface ExcelConsumer<T, S> {

    public void apply(T t, S u);

}