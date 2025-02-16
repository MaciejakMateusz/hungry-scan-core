package com.hackybear.hungry_scan_core.interfaces;

@FunctionalInterface
public interface ThrowingBiSupplier<T, R, U> {
    U get(T t, R r) throws Exception;
}