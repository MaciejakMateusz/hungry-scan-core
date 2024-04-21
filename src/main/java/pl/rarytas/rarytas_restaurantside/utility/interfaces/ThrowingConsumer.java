package pl.rarytas.rarytas_restaurantside.utility.interfaces;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws Exception;
}