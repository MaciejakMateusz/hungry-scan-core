package com.hackybear.hungry_scan_core.interfaces.aggregators;

public interface MenuItemViewAggregation {
    Long id();

    String getDefaultTranslation();

    String getTranslationEn();

    Integer views();
}