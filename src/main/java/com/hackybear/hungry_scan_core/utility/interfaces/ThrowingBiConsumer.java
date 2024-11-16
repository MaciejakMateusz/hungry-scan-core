package com.hackybear.hungry_scan_core.utility.interfaces;

@FunctionalInterface
public interface ThrowingBiConsumer<T, R> {
    void accept(T t, R r) throws Exception;
}