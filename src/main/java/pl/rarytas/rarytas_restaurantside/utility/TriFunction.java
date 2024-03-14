package pl.rarytas.rarytas_restaurantside.utility;

@FunctionalInterface
public interface TriFunction<P, SD, ED, L> {
    L apply(P pageable, SD startDate, ED endDate);
}