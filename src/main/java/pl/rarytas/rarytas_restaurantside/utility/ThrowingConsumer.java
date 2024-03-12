package pl.rarytas.rarytas_restaurantside.utility;

import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws LocalizedException;
}