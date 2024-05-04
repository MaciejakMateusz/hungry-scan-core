package pl.rarytas.hungry_scan_core.utility.interfaces;

import pl.rarytas.hungry_scan_core.exception.LocalizedException;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws LocalizedException;
}