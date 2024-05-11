package pl.rarytas.hungry_scan_core.utility.interfaces;

import pl.rarytas.hungry_scan_core.exception.LocalizedException;

@FunctionalInterface
public interface ThrowingBiConsumer<T, R> {
    void accept(T t, R r) throws LocalizedException;
}