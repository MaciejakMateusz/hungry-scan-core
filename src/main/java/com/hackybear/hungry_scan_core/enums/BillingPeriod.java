package com.hackybear.hungry_scan_core.enums;

import lombok.Getter;

@Getter
public enum BillingPeriod {
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String methodName;

    BillingPeriod(String methodName) {
        this.methodName = methodName;
    }
}
