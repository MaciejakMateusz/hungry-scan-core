package com.hackybear.hungry_scan_core.utility.interfaces;

import com.hackybear.hungry_scan_core.exception.LocalizedException;

@FunctionalInterface
public interface ThrowingBiConsumer<T, R> {
    void accept(T t, R r) throws LocalizedException;
}