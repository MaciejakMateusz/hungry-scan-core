package pl.rarytas.hungry_scan_core.utility.interfaces;

@FunctionalInterface
public interface TriFunction<P, DF, DT, L> {
    L apply(P pageable, DF datFrom, DT dateTo);
}