package pl.rarytas.rarytas_restaurantside.utility.interfaces;

import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws LocalizedException;
}