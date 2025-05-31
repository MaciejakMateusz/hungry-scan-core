package com.hackybear.hungry_scan_core.interfaces.aggregators;

public interface ScanAggregation {

    Integer getPeriod();

    Integer getTotal();

    Integer getUniqueCount();

}