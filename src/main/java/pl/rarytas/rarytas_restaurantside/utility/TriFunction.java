package pl.rarytas.rarytas_restaurantside.utility;

@FunctionalInterface
public interface TriFunction<P, DF, DT, L> {
    L apply(P pageable, DF datFrom, DT dateTo);
}