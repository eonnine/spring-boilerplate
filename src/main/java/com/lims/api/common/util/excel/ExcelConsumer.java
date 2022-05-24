package com.lims.api.common.util.excel;

@FunctionalInterface
public interface ExcelConsumer<T, U> {

    public void apply(T t, U u);

}