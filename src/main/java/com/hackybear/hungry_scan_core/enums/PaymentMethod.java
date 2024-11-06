package com.hackybear.hungry_scan_core.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH("cash"),
    CARD("card"),
    BLIK("blik"),
    APPLE("apple");

    private final String methodName;

    PaymentMethod(String methodName) {
        this.methodName = methodName;
    }
}
