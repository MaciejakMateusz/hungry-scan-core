package pl.rarytas.rarytas_restaurantside.utility.interfaces;

import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

@FunctionalInterface
public interface ThrowingBiConsumer<T, R> {
    void accept(T t, R r) throws LocalizedException;
}