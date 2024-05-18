package com.hackybear.hungry_scan_core.utility.interfaces;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}