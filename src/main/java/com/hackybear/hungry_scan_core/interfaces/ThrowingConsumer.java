package com.hackybear.hungry_scan_core.interfaces;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws Exception;
}