package pl.rarytas.rarytas_restaurantside.utility;

import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws LocalizedException;
}