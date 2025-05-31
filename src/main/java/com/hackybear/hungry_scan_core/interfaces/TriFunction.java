package com.hackybear.hungry_scan_core.interfaces;

@FunctionalInterface
public interface TriFunction<P, DF, DT, L> {

    L apply(P pageable, DF datFrom, DT dateTo);

}