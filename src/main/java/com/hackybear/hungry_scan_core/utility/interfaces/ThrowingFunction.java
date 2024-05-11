package com.hackybear.hungry_scan_core.utility.interfaces;

import com.hackybear.hungry_scan_core.exception.LocalizedException;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws LocalizedException;
}